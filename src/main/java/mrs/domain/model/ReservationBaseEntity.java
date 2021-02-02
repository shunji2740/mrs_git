package mrs.domain.model;

import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.ListArrayType;

import mrs.app.user.User;

@TypeDefs({
		@TypeDef(name = "string-array", typeClass = ListArrayType.class),
		@TypeDef(name = "int-array", typeClass = IntArrayType.class),
		@TypeDef(name = "list-array", typeClass = ListArrayType.class)
})

@MappedSuperclass
public class ReservationBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reservationId;

	private LocalTime startTime;
	private LocalTime endTime;
	private String notificationMailCheck;

	/*
	 * reservableRoomのフィールドにReservableRoomIdインスタンスがあり、
	 * ReservableRoomIdのフィールドにroomIdとreservedDateがある
	 */
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "reserved_date"), @JoinColumn(name = "room_id") })
	private ReservableRoom reservableRoom;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	//1件でもtrueが返された場合はtrue(重複あり)となる
	public boolean overlap(Reservation target) {
		//自身のreservableroomIdとtargetのreservableroomIdを比較し、重複判定を行っている
		if (!Objects.equals(this.reservableRoom.getReservableRoomId(),
				target.getReservableRoom().getReservableRoomId())) {
			//reservableroomIdが違った時点で予約可能なのでfalseを返す
			return false;
		}

		//reservableroomIdが同じなら、時間帯で判定する
		//予約時間が重複していないかを判定
		if (this.startTime.equals(target.getStartTime()) && this.endTime.equals(target.getEndTime())) {
			return true;
		}

		return target.getEndTime().isAfter(this.startTime) && this.endTime.isAfter(target.getStartTime());
	}

	public Integer getReservationId() {
		return reservationId;
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getNotificationMailCheck() {
		return notificationMailCheck;
	}

	public void setNotificationMailCheck(String notificationMailCheck) {
		this.notificationMailCheck = notificationMailCheck;
	}

	public ReservableRoom getReservableRoom() {
		return reservableRoom;
	}

	public void setReservableRoom(ReservableRoom reservableRoom) {
		this.reservableRoom = reservableRoom;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}