package mrs.domain.service.reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import mrs.app.reservation.ReservationForm;
import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.model.RoleName;
import mrs.domain.model.User;
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

	//引数reservationには予約情報のすべてが格納されている(予約したい状態)
	public Reservation checkReservation(Reservation reservation) {
		
		//複合キー(会議室IDと指定日)を取得
		ReservableRoomId reservableRoomId = reservation.getReservableRoom().getReservableRoomId();

		//複合キーによって指定された日の予約可能な指定会議室を取得
		//ここで初めてreservableroomに登録されているかどうかを識別している
		ReservableRoom reservable = reservableRoomRepository.findOneForUpdateByReservableRoomId(reservableRoomId);

		//指定日の指定会議室が取得できなかったら場合
		if (reservable == null) {
			//例外ステートメントをスローする
			throw new UnavailableReservationException("その日の指定会議室は使えません。");
		}

		/*	overlapメソッドにて重複チェック
			１件でもtrueが返された場合は重複と判定
			ここでは指定の日の時間帯が予約可能かを判定している
			[処理の流れ]
			①reservableRoomId(指定の日に使える指定の会議室ID)を引数に
			findByReservableRoom_ReservableRoomIdOrderByStartTimeAscで
			その日指定の会議室の一覧をList<Reservation>で返す。
			②stream()を使い、１個づつ重複がないかを確認していく。
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

	//予約情報の登録
	public void reserve(Reservation reservation) {
		reservationRepository.save(reservation);

	}

	//予約リストを返却
	public List<Reservation> findReservations(ReservableRoomId reservableRoomId) {

		return reservationRepository.findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId);
	}

	//予約キャンセルメソッド
	public Boolean checkCancel(Integer reservationId, User requestUser) {
		Reservation reservation = reservationRepository.findOneForUpdateByReservationId(reservationId);

		if (RoleName.ADMIN != requestUser.getRoleName()
				&& !Objects.equals(reservation.getUser().getUserId(), requestUser.getUserId())) {

			throw new AccessDeniedException("要求されたキャンセルは許可できません。");
		}

		reservationRepository.delete(reservation);

		return true;
	}

	//予約キャンセル完了
	public void cancell(Integer reservationId, User requestUser) {

		Reservation reservation = reservationRepository.findOneForUpdateByReservationId(reservationId);

		if (RoleName.ADMIN != requestUser.getRoleName()
				&& !Objects.equals(reservation.getUser().getUserId(), requestUser.getUserId())) {

			throw new AccessDeniedException("要求されたキャンセルは許可できません。");
		}

		reservationRepository.delete(reservation);
	}

	//メール通知メソッド
	public void sendNotificationMail(ReservationForm form, Reservation reservation, Map<UUID, Timer> mapIdForTimer)
			throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

		Timer timer = mapIdForTimer.get(reservation.getReservationIdForTimer());
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SimpleMailMessage msg = new SimpleMailMessage();
				msg.setFrom("shunjimunemoto@gmail.com");
				msg.setTo("shunjimunemoto@gmail.com");
				msg.setText("ご予約時間の30分前となりました。"
						+ "\n\n------------------------------------------\n" +
						"\n------------------------------------------");
				mailSender.send(msg);
				timer.cancel();
			}
		};

		LocalDateTime dt = LocalDateTime.of(form.getDate(), form.getStartTime());
		//30分引く
		dt = dt.minusMinutes(10);
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String dtFormated = dt.format(dateTimeFormatter);

		//timer.schedule(task, sdf.parse("2021/01/17 21:36"));
		timer.schedule(task, sdf.parse(dtFormated));

		//予約番号をセットする
		reservation.setReservationIdForTimer(UUID.randomUUID());
		//予約番号、timerオブジェクトをmapに格納
		mapIdForTimer.put(reservation.getReservationIdForTimer(), timer);

	}

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

	public Map<String, Integer> createMapCategoryForQuantity(int[] cateringQuantity,
			List<String> selectedCateringStrs) {

		Map<String, Integer> cateringMapCategoryForQuantity = new HashMap<>();

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
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

		// forEachのパターン
		cateringMapCategoryForQuantity.forEach((k, v) -> {
			System.out.println(k);
			System.out.println(v);
		});

		System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");

		return cateringMapCategoryForQuantity;
	}
}
