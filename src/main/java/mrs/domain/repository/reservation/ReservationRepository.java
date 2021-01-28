package mrs.domain.repository.reservation;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;


@Transactional
public interface ReservationRepository extends JpaRepository<Reservation, Integer>{

	List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(ReservableRoomId reservableRoomId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Reservation findOneForUpdateByReservationId(Integer reservationId);

	void delete(Reservation reservation);

}
