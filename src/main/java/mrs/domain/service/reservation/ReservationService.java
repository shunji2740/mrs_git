package mrs.domain.service.reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mrs.app.user.User;
import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.repository.reservation.ReservationRepository;
import mrs.domain.repository.room.ReservableRoomRepository;

@Service
@Transactional
public class ReservationService {

	@Autowired
	ReservationRepository reservationRepository;
	@Autowired
	ReservableRoomRepository reservableRoomRepository;
	@Autowired
	private MailSender mailSender;

	//予約可能か判定するメソッド
	public Reservation checkReservation(Reservation reservation) {

		//複合キー(会議室IDと指定日)を取得
		ReservableRoomId reservableRoomId = reservation.getReservableRoom().getReservableRoomId();

		//予約指定日が現在より前の場合
		if (reservableRoomId.getReservedDate().isBefore(LocalDate.now())) {
			//例外ステートメントをスローする
			throw new UnavailableReservationException("その日付は指定できません");
		}

		//複合キーによって指定された日＆予約可能な指定会議室を取得
		ReservableRoom reservable = reservableRoomRepository.findOneForUpdateByReservableRoomId(reservableRoomId);

		//指定日の指定会議室が取得できなかったら場合
		if (reservable == null) {
			//例外ステートメントをスローする
			throw new UnavailableReservationException("その日の指定会議室は使えません。");
		}

		/*	overlapメソッドにて重複チェック
			１件でもtrueが返された場合は重複と判定
			[処理の流れ]
			①reservableRoomId(複合キー)を引数に
			findByReservableRoom_ReservableRoomIdOrderByStartTimeAscで
			指定日＆指定会議室の予約一覧をList<Reservation>で返す
			②overlap()で１個づつ重複がないかを確認していく
		*/
		boolean overlap = reservationRepository
				.findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId)
				.stream().anyMatch(x -> x.overlap(reservation));

		if (overlap) {
			//例外ステートメントをスローする
			throw new AlreadyReservedException("指定日は既に予約されています。");
		}

		return reservation;

	}

	//予約情報の登録メソッド
	public void reserve(Reservation reservation) {
		reservationRepository.save(reservation);

	}

	//予約リストを返却メソッド
	public List<Reservation> findReservations(ReservableRoomId reservableRoomId) {

		return reservationRepository.findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId);
	}

	//予約キャンセルメソッド
	public Boolean checkCancel(Integer reservationId, User requestUser) {

		Reservation reservation = reservationRepository.findOneForUpdateByReservationId(reservationId);

		if (!"ADMIN".equals(requestUser.getRoleName())
				&& !Objects.equals(reservation.getUser().getUserId(), requestUser.getUserId())) {
			throw new AccessDeniedException("要求されたキャンセルは許可できません。");
		}

		reservationRepository.delete(reservationRepository.findOneForUpdateByReservationId(reservationId));

		return true;
	}

	//予約キャンセル完了メソッド
	public void cancell(Integer reservationId, User requestUser) {

		Reservation reservation = reservationRepository.findOneForUpdateByReservationId(reservationId);

		if (!"ADMIN".equals(requestUser.getRoleName())
				&& !Objects.equals(reservation.getUser().getUserId(), requestUser.getUserId())) {

			throw new AccessDeniedException("要求されたキャンセルは許可できません。");
		}

		reservationRepository.delete(reservation);
	}

	//予約情報通知メール送信メソッド
	public void sendInfoMail(Reservation reservation) {
		String selectedCatering = "";

		for(String cateringService : reservation.getCateringSelection()) {
			selectedCatering = selectedCatering + cateringService + "、";
		}
		selectedCatering = selectedCatering.substring(0, selectedCatering.length());

		String body = "お名前: " + reservation.getUser().getFirstName() + "\n" +
		"メールアドレス: " + reservation.getUser().getUserId() + "\n" +
		"ご予約内容: \n" +
		"ご予約時間: " + reservation.getStartTime() + "～" + reservation.getEndTime() + "\n" +
		"追加備品: " + reservation.getAdditionalEquipments() + "\n" +
		"合計金額: " + reservation.getTotalPrice() + "円" + "\n" +
		"お支払方法: " + reservation.getSelectedPaymentMethod();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(reservation.getUser().getUserId());
		msg.setTo("shunjimunemoto@gmail.com");
		msg.setText("ご予約内容は下記の通りです。\n\n------------------------------------------\n" + body
				+ "\n------------------------------------------");
		mailSender.send(msg);
	}

	//メール通知メソッド
	public void sendNotificationMail(Reservation reservation, Map<UUID, Timer> mapIdForTimer)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		String body = "お名前: " + reservation.getUser().getFirstName() + "\n" +
		"メールアドレス: " + reservation.getUser().getUserId() + "\n" +
		"ご予約内容: \n" +
		"ご予約時間: " + reservation.getStartTime() + "～" + reservation.getEndTime() + "\n" +
		"追加備品: " + reservation.getAdditionalEquipments() + "\n" +
		"合計金額: " + reservation.getTotalPrice() + "円" + "\n" +
		"お支払方法: " + reservation.getSelectedPaymentMethod();


		Timer timer = mapIdForTimer.get(reservation.getReservationIdForTimer());
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SimpleMailMessage msg = new SimpleMailMessage();
				msg.setFrom("shunjimunemoto@gmail.com");
				msg.setTo("shunjimunemoto@gmail.com");
				msg.setText("ご予約時間の30分前となりました。"
						+ "\n\n------------------------------------------\n" + body
						+ "\n------------------------------------------");
				mailSender.send(msg);
				timer.cancel();
			}
		};

		LocalDateTime dt = LocalDateTime.of(reservation.getReservableRoom().getReservableRoomId().getReservedDate(),
				reservation.getStartTime());
		//30分引く
		dt = dt.minusMinutes(30);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String dtFormated = dt.format(dateTimeFormatter);
		timer.schedule(task, sdf.parse(dtFormated));

		//予約番号をセットする
		reservation.setReservationIdForTimer(UUID.randomUUID());
		//予約番号、timerオブジェクトをmapに格納
		mapIdForTimer.put(reservation.getReservationIdForTimer(), timer);

	}

	//各ケータリングの価格を計算するメソッド
	public int calculateCateringPrice(int[] cateringQuantity, List<String> selectedCateringStrs) {
		int totalPrice = 0;

		for (String selectedCateringStr : selectedCateringStrs) {
			switch (selectedCateringStr) {
			case "お弁当":
				totalPrice = totalPrice + (cateringQuantity[0] * 1000);
				System.out.println("お弁当" + totalPrice);
				break;
			case "ポットコーヒー":
				totalPrice = totalPrice + (cateringQuantity[1] * 100);
				System.out.println("ポットコーヒー" + totalPrice);
				break;
			case "お茶ペットボトル":
				totalPrice = totalPrice + (cateringQuantity[2] * 170);
				System.out.println("お茶ペットボトル" + totalPrice);
				break;
			case "水ペットボトル":
				totalPrice = totalPrice + (cateringQuantity[3] * 150);
				System.out.println("水ペットボトル" + totalPrice);
				break;
			}
		}

		return totalPrice;
	}

	//各ケータリングの注文数量をMapに格納するメソッド
	public Map<String, Integer> createMapCategoryForQuantity(int[] cateringQuantity,
			List<String> selectedCateringStrs) {

		Map<String, Integer> cateringMapCategoryForQuantity = new HashMap<>();

		for (String selectedCateringStr : selectedCateringStrs) {
			switch (selectedCateringStr) {
			case "お弁当":
				if (cateringQuantity[0] != 0) {
					cateringMapCategoryForQuantity.put(selectedCateringStr, cateringQuantity[0]);
				}
				break;
			case "ポットコーヒー":
				if (cateringQuantity[1] != 0) {
					cateringMapCategoryForQuantity.put(selectedCateringStr, cateringQuantity[1]);
				}
				break;
			case "お茶ペットボトル":
				if (cateringQuantity[2] != 0) {
					cateringMapCategoryForQuantity.put(selectedCateringStr, cateringQuantity[2]);
				}
				break;
			case "水ペットボトル":
				if (cateringQuantity[3] != 0) {
					cateringMapCategoryForQuantity.put(selectedCateringStr, cateringQuantity[3]);
				}
				break;
			}
		}

		return cateringMapCategoryForQuantity;
	}
}
