package mrs.domain.service.reservation;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
