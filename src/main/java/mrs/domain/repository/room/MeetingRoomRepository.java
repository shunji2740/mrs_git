package mrs.domain.repository.room;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import mrs.domain.model.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Integer> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	MeetingRoom findOneForUpdateByRoomId(Integer roomId);

}
