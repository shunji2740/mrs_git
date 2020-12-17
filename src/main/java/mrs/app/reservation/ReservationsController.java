package mrs.app.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.model.RoleName;
import mrs.domain.model.User;
import mrs.domain.service.reservation.AlreadyReservedException;
import mrs.domain.service.reservation.ReservationService;
import mrs.domain.service.reservation.UnavailableReservationException;
import mrs.domain.service.room.RoomService;

@Controller
@RequestMapping("reservations/{date}/{roomId}")
public class ReservationsController {

	@Autowired
	RoomService roomService;
	@Autowired
	ReservationService reservationService;

	//予約確認・予約一覧画面
	//いわゆる確認画面だけでまだ予約は完了していない、予約可能かの確認作業はreserve()にて
	@RequestMapping(method = RequestMethod.GET)
	String reserveForm(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
			@PathVariable("roomId") Integer roomId, Model model) {

		//指定日かつ指定会議室のreservableRoomIdを生成
		ReservableRoomId reservableRoomId = new ReservableRoomId(roomId, date);

		//指定日かつ指定会議室の予約リストを取得
		List<Reservation> reservations = reservationService.findReservations(reservableRoomId);

		//LocalDateオブジェクトを作成してリストに格納する
		List<LocalTime> timeList = Stream.iterate(LocalTime.of(0, 0), t -> t.plusMinutes(30))
				.limit(24 * 2)
				.collect(Collectors.toList());

		model.addAttribute("room", roomService.findMeetingRomm(roomId));
		model.addAttribute("reservations", reservations);
		model.addAttribute("timeList", timeList);
		model.addAttribute("user", dummyUser());

		return "reservation/reserveForm";

	}

	//予約処理・予約可能かの処理
	@RequestMapping(method = RequestMethod.POST)
	String reserve(@Validated ReservationForm form, BindingResult bindingResult,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
			@PathVariable("roomId") Integer roomId, Model model) {

		if (bindingResult.hasErrors()) {
			return reserveForm(date, roomId, model);
		}

		Reservation reservation = new Reservation();
		reservation.setStartTime(form.getStartTime());
		reservation.setEndTime(form.getEndTime());
		ReservableRoom reservableRoom = new ReservableRoom(new ReservableRoomId(roomId, date));
		reservation.setReservableRoom(reservableRoom);
		reservation.setUser(dummyUser());

		try {
			//★ここではじめてreservable_roomに登録されているか、重複していないかをチェック
			reservationService.reserve(reservation);

		} catch (UnavailableReservationException | AlreadyReservedException e) {
			model.addAttribute("error", e.getMessage());
			return reserveForm(date, roomId, model);
		}

		return "redirect:/reservations/{date}/{roomId}";
	}


	//予約削除
	@RequestMapping(method = RequestMethod.POST, params = "cancel")
	String cancel(@RequestParam("reservationId") Integer reservationId, @PathVariable("roomId") Integer roomId,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date")LocalDate date, Model model) {

		System.out.println("------------test-----------");
		//まだダミーユーザー
		User user = dummyUser();
		try {
			reservationService.cancel(reservationId, user);

		}catch(IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return reserveForm(date, roomId, model);
		}

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

	//ダミーユーザ作成メソッド
	private User dummyUser() {
		User user = new User();
		user.setUserId("taro-yamada");
		user.setFirstName("太郎");
		user.setLastName("山田");
		user.setRoleName(RoleName.USER);
		return user;
	}

}