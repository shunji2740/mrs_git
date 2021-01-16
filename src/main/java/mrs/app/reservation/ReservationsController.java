package mrs.app.reservation;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.model.User;
import mrs.domain.repository.reservation.ReservationRepository;
import mrs.domain.service.reservation.AlreadyReservedException;
import mrs.domain.service.reservation.ReservationService;
import mrs.domain.service.reservation.UnavailableReservationException;
import mrs.domain.service.room.RoomService;
import mrs.domain.service.user.ReservationUserDetails;

@Controller
@RequestMapping("reservations/{date}/{roomId}")
public class ReservationsController {

	@Autowired
	RoomService roomService;

	@Autowired
	ReservationService reservationService;

	@Autowired
	ReservationRepository reservationRepository;

	@Autowired
	HttpSession session;

	//予約確認・予約一覧画面
	//予約可能かの確認作業はreserve()にて
	@RequestMapping(method = RequestMethod.GET)
	String reserveForm(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
			@PathVariable("roomId") Integer roomId, Model model) {

		ReservableRoomId reservableRoomId;
		LocalDate selected_date = (LocalDate) model.getAttribute("schedule_date");
		reservableRoomId = new ReservableRoomId(roomId, selected_date);

		//指定日かつ指定会議室の予約リストを取得
		List<Reservation> reservations = reservationService.findReservations(reservableRoomId);

		//LocalDateオブジェクトを作成してリストに格納する
		List<LocalTime> timeList = Stream.iterate(LocalTime.of(0, 0), t -> t.plusMinutes(30))
				.limit(24 * 2)
				.collect(Collectors.toList());

		model.addAttribute("room", roomService.findMeetingRomm(roomId));
		model.addAttribute("reservations", reservations);
		model.addAttribute("timeList", timeList);

		// Flash Scopeから値の取り出し
		Boolean booleanResult = (Boolean) model.getAttribute("booleanResult");
		model.addAttribute("booleanResult", booleanResult);
		String message = (String) model.getAttribute("message");
		model.addAttribute("message", message);

		return "reservation/reserveForm";
	}

	@RequestMapping(method = RequestMethod.POST, params = "schedule")
	String confirmSchedule(@Validated ReservationForm form,
			RedirectAttributes redirectAttributes, Model model) {

		LocalDate schedule_date = form.getDate();
		redirectAttributes.addFlashAttribute("schedule_date", schedule_date);

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約処理・予約可能かの処理
	@RequestMapping(method = RequestMethod.POST)
	String reserve(@Validated ReservationForm form, BindingResult bindingResult,
			@AuthenticationPrincipal ReservationUserDetails userDetails,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
			@PathVariable("roomId") Integer roomId, Model model) {

		if (bindingResult.hasErrors()) {
			return reserveForm(date, roomId, model);

		}

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★");
		System.out.println("→" + form.getInputSingleCheck());
		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★");


		Reservation reservation = new Reservation();
		reservation.setStartTime(form.getStartTime());
		reservation.setEndTime(form.getEndTime());
		reservation.setInputSingleCheck(form.getInputSingleCheck());
		ReservableRoom reservableRoom = new ReservableRoom(new ReservableRoomId(roomId, form.getDate()));
		reservation.setReservableRoom(reservableRoom);
		reservation.setUser(userDetails.getUser());

		SendMailAfterThirtyMinitues sendMailAfterThirtyMinitues = new SendMailAfterThirtyMinitues();

		try {
			sendMailAfterThirtyMinitues.sendMailAfterThirtyMinitues(reservation);
		} catch (ParseException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}



		try {
			//★ここではじめてreservable_roomに登録されているか、重複していないかをチェックする
			reservationService.checkReservation(reservation);

		} catch (UnavailableReservationException | AlreadyReservedException e) {
			model.addAttribute("error", e.getMessage());
			return reserveForm(date, roomId, model);
		}

		session.setAttribute("reservation", reservation);

		model.addAttribute("reservation", reservation);

		return "reservation/confirmReservation";
	}

	//予約完了
	@RequestMapping(method = RequestMethod.POST, params = "confirmedReservation")
	String confirmedReservation(RedirectAttributes redirectAttributes, Model model) {

		//セッションからreservationエンティティを取得
		Reservation reservation = (Reservation) session.getAttribute("reservation");

		//予約を登録する
		reservationService.reserve(reservation);

		//リダイレクト先に値を渡す
		redirectAttributes.addFlashAttribute("message", "予約が完了しました");
		redirectAttributes.addFlashAttribute("booleanResult", true);

		//セッションを開放する
		session.removeAttribute("reservation");

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約削除可能かの処理
	@RequestMapping(method = RequestMethod.POST, params = "cancel")
	String cancel(@RequestParam("reservationId") Integer reservationId,
			@AuthenticationPrincipal ReservationUserDetails userDetails,
			@PathVariable("roomId") Integer roomId,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date, Model model) {

		Reservation reservation = new Reservation();

		//削除するreservationエンティティを取得・格納
		reservation = reservationRepository.findOneForUpdateByReservationId(reservationId);

		//ユーザー取得
		User user = userDetails.getUser();

		model.addAttribute("reservation", reservation);

		//セッションにuser,reservationIdをセットする
		session.setAttribute("user", user);
		session.setAttribute("reservationId", reservationId);

		return "reservation/confirmCancellation";
	}

	//予約キャンセル完了
	@RequestMapping(method = RequestMethod.POST, params = "confirmedCancellation")
	String confirmedCancellation(RedirectAttributes redirectAttributes, Model model) {

		User user = (User) session.getAttribute("user");
		Integer reservationId = (Integer) session.getAttribute("reservationId");

		//予約キャンセル
		reservationService.checkCancel(reservationId, user);

		redirectAttributes.addFlashAttribute("message", "予約がキャンセルされました");
		redirectAttributes.addFlashAttribute("booleanResult", true);

		//セッションからuser,reservationIdを解放する
		session.removeAttribute("user");
		session.removeAttribute("reservationId");

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約時間のデフォルトForm
	@ModelAttribute
	ReservationForm setUpForm() {
		ReservationForm form = new ReservationForm();
		//デフォルト値
		form.setStartTime(LocalTime.of(9, 0));
		form.setEndTime(LocalTime.of(10, 0));

		return form;
	}
}
