package mrs.app.reservation;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import mrs.app.user.User;
import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
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

	static Map<UUID, Timer> mapIdForTimer = new HashMap<>();

	//予約確認・予約一覧画面
	//予約可能かの確認作業はreserve()にて
	@RequestMapping(method = RequestMethod.GET)
	String reserveForm(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
			@PathVariable("roomId") Integer roomId, Model model) {

		ReservableRoomId reservableRoomId;

		// Flash Scopeからリダイレクトされた値の取り出し
		LocalDate selectedDay = (LocalDate) model.getAttribute("selectedDay");

		reservableRoomId = new ReservableRoomId(roomId, selectedDay);

		//reservableRoomIdを使用し、指定日・指定会議室の予約リストを取得
		List<Reservation> reservationListOfTheDay = reservationService.findReservations(reservableRoomId);

		//30分間隔の23:30までのLocalTimeリストを作成
		List<LocalTime> timeList = Stream.iterate(LocalTime.of(0, 0), t -> t.plusMinutes(30))
				.limit(24 * 2)
				.collect(Collectors.toList());

		model.addAttribute("room", roomService.findMeetingRomm(roomId));
		model.addAttribute("reservationListOfTheDay", reservationListOfTheDay);
		model.addAttribute("timeList", timeList);

		// Flash Scopeからリダイレクトされた値の取り出し
		//予約またはキャンセル完了の際、モーダルウィンドウに表示するメッセージとTRUE値をmodelにセット
		Boolean booleanResult = (Boolean) model.getAttribute("booleanResult");
		model.addAttribute("booleanResult", booleanResult);
		String message = (String) model.getAttribute("message");
		model.addAttribute("message", message);

		return "reservation/reserveForm";
	}

	/*
	 * 選択された日付をreserveFormにリダイレクトするメソッド
	 */
	@RequestMapping(method = RequestMethod.POST, params = "schedule")
	String confirmSchedule(ReservationForm form, RedirectAttributes redirectAttributes, Model model) {

		LocalDate selectedDay = form.getDate();
		redirectAttributes.addFlashAttribute("selectedDay", selectedDay);

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約処理・予約可能かの処理するメソッド
	@SuppressWarnings("null")
	@RequestMapping(method = RequestMethod.POST)
	String reserve(@Validated ReservationForm form, BindingResult bindingResult,
			@AuthenticationPrincipal ReservationUserDetails userDetails,
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
			@PathVariable("roomId") Integer roomId,
			@RequestParam(value = "equipments", required = false) List<String> additionalEquipments,
			@RequestParam(value = "fdn", required = false) int[] cateringQuantity,
			@RequestParam(value = "fd", required = false) List<String> selectedCateringStrs,
			@RequestParam(value = "notificationMailCheck", required = false) String notificationMailCheck,
			Model model) {

		if (additionalEquipments == null) {
			List<String> additionalEquipments1 = new ArrayList<>();
			additionalEquipments1.add("なし");
			additionalEquipments = additionalEquipments1;
			System.out.println(additionalEquipments.get(0));
		}

		if (selectedCateringStrs == null || Arrays.stream(cateringQuantity).allMatch(val -> val == 0)) {
			List<String> selectedCateringStrs1 = new ArrayList<>();
			selectedCateringStrs1.add("なし");
			selectedCateringStrs = selectedCateringStrs1;
		}

		//合計金額を格納する変数
		int totalPrice = 0;

		Reservation reservation = new Reservation();
		reservation.setStartTime(form.getStartTime());
		reservation.setEndTime(form.getEndTime());
		ReservableRoom reservableRoom = new ReservableRoom(new ReservableRoomId(roomId, form.getDate()));
		reservation.setReservableRoom(reservableRoom);
		reservation.setUser(userDetails.getUser());
		reservation.setCateringQuantity(cateringQuantity);
		reservation.setCateringSelection(selectedCateringStrs);
		reservation.setSelectedPaymentMethod(form.getSelectedPaymentMethod());

		//★ここではじめてreservable_roomに登録されているか、重複していないかをチェックする
		try {
			if(reservation.getReservableRoom().getReservableRoomId().getReservedDate() != null) {
				reservationService.checkReservation(reservation);
			}

		} catch (UnavailableReservationException | AlreadyReservedException e) {
			model.addAttribute("error", e.getMessage());
			return reserveForm(date, roomId, model);
		}

		//validationチェック
		if (bindingResult.hasErrors()) {
			return reserveForm(date, roomId, model);
		}

		//予約通知の設定
		if (form.getNotificationMailCheck() != null) {
			reservation.setNotificationMailCheck(form.getNotificationMailCheck());
		} else {
			reservation.setNotificationMailCheck("not checked");
		}

		//追加設備を格納
		if (additionalEquipments != null) {
			reservation.setAdditionalEquipments(additionalEquipments);
		}

		//Mapにケータリングの種類、数量を格納およびmodelに格納
		if (cateringQuantity != null && selectedCateringStrs != null) {
			Map<String, Integer> cateringMapCategoryForQuantity = reservationService
					.createMapCategoryForQuantity(cateringQuantity, selectedCateringStrs);
			model.addAttribute("cateringMap", cateringMapCategoryForQuantity);
			//ケータリングの合計金額を計算する
			totalPrice = reservationService.calculateCateringPrice(cateringQuantity, selectedCateringStrs);
		}

		//予約時間の合計金額の計算する
		long localDiffDays5 = ChronoUnit.MINUTES.between(form.getStartTime(), form.getEndTime());
		totalPrice = (int) ((int) totalPrice + ((localDiffDays5 / 30) * 500));
		reservation.setTotalPrice(totalPrice);

		//予約番号をセットする
		reservation.setReservationIdForTimer(UUID.randomUUID());
		//Timer宣言
		Timer timer = new Timer(false);
		//予約番号、timerオブジェクトをmapに格納
		mapIdForTimer.put(reservation.getReservationIdForTimer(), timer);

		session.setAttribute("reservation", reservation);
		model.addAttribute("reservation", reservation);

		return "reservation/confirmReservation";
	}

	//予約完了(現金払いの場合)処理メソッド
	@RequestMapping(method = RequestMethod.POST, params = "confirmedReservation")
	String confirmedReservation(RedirectAttributes redirectAttributes, Model model) {
		//セッションからreservationエンティティを取得
		Reservation reservation = (Reservation) session.getAttribute("reservation");
		//予約を登録する
		reservationService.reserve(reservation);

		try {

			if (reservation.getNotificationMailCheck().equals("checked")) {
				reservationService.sendNotificationMail(reservation, mapIdForTimer);
			}
			reservationService.sendInfoMail(reservation);

		} catch (ParseException e2) {
			e2.printStackTrace();
		}

		//リダイレクト先に値を渡す
		redirectAttributes.addFlashAttribute("message", "予約が完了しました");
		redirectAttributes.addFlashAttribute("booleanResult", true);
		//セッションを開放する
		session.removeAttribute("reservation");

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約完了(クレジットカード決済の場合)処理メソッド
	@RequestMapping(method = RequestMethod.POST, params = "confirmedReservationCredit")
	String confirmedReservationCredit(
			@RequestParam("stripeToken") String stripeToken,
			@RequestParam("stripeTokenType") String stripeTokenType,
			@RequestParam("stripeEmail") String stripeEmail,
			RedirectAttributes redirectAttributes, Model model) {

		//セッションからreservationエンティティを取得
		Reservation reservation = (Reservation) session.getAttribute("reservation");

		try {

			if (reservation.getNotificationMailCheck().equals("checked")) {
				reservationService.sendNotificationMail(reservation, mapIdForTimer);
			}
			reservationService.sendInfoMail(reservation);

		} catch (ParseException e2) {
			e2.printStackTrace();
		}

		Stripe.apiKey = "sk_test_51IBK2tBYXTAwdZzcBTT70XqkVatAilqmwW7Ogt3mxF3TbtmLnJe5sA7JmIw3kAmmIa7rxBWeoKR5OOjc2AJBst9C001NQ16bBt";

		Map<String, Object> chargeMap = new HashMap<String, Object>();
		chargeMap.put("amount", reservation.getTotalPrice());
		chargeMap.put("description", "ご利用金額");
		chargeMap.put("currency", "jpy");
		chargeMap.put("source", stripeToken);

		try {
			Charge charge = Charge.create(chargeMap);
			System.out.println(charge);
		} catch (StripeException e) {
			e.printStackTrace();
		}

		ResponseEntity.ok().build();

		//予約を登録する
		reservationService.reserve(reservation);

		//リダイレクト先に値を渡す
		redirectAttributes.addFlashAttribute("message", "予約が完了しました");
		redirectAttributes.addFlashAttribute("booleanResult", true);

		//セッションを開放する
		session.removeAttribute("reservation");

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約削除可能かの処理メソッド
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

	//予約キャンセル完了処理メソッド
	@RequestMapping(method = RequestMethod.POST, params = "confirmedCancellation")
	String confirmedCancellation(RedirectAttributes redirectAttributes, Model model) {

		User user = (User) session.getAttribute("user");
		Integer reservationId = (Integer) session.getAttribute("reservationId");

		//削除するreservationエンティティを取得・格納
		Reservation reservation = new Reservation();
		reservation = reservationRepository.findOneForUpdateByReservationId(reservationId);

		Timer timer = mapIdForTimer.get(reservation.getReservationIdForTimer());
		timer.cancel();

		//予約キャンセル
		reservationService.checkCancel(reservationId, user);

		redirectAttributes.addFlashAttribute("message", "予約がキャンセルされました");
		redirectAttributes.addFlashAttribute("booleanResult", true);

		//セッションからuser,reservationIdを解放する
		session.removeAttribute("user");
		session.removeAttribute("reservationId");

		return "redirect:/reservations/{date}/{roomId}";
	}

	//予約時間のデフォルトForm作成メソッド
	@ModelAttribute
	ReservationForm setUpForm() {
		ReservationForm form = new ReservationForm();
		//デフォルト値
		form.setStartTime(LocalTime.of(9, 0));
		form.setEndTime(LocalTime.of(10, 0));

		return form;
	}

}
