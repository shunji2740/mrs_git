package mrs.domain.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.ListArrayType;

import mrs.app.reservation.ReservationForm;
import mrs.app.user.User;

@TypeDefs({
		@TypeDef(name = "string-array", typeClass = ListArrayType.class),
		@TypeDef(name = "int-array", typeClass = IntArrayType.class),
		@TypeDef(name = "list-array", typeClass = ListArrayType.class)
})

@Entity
public class Reservation extends ReservationForm implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reservationId;

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "reserved_date"), @JoinColumn(name = "room_id") })
	/*reserved_date,room_idが格納されている
	(reservableRoomのフィールドにReservableRoomIdクラスがあり、
	ReservableRoomIdのフィールドに
	roomIdとreservedDateがある)*/
	private ReservableRoom reservableRoom;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	//1件でもtrueが返された場合はtrue(重複あり)となる
	public boolean overlap(Reservation target) {
		//自分自身のreservableroomIdとtargetのreservableroomIdを比較し、重複判定を行っている
		if (!Objects.equals(this.reservableRoom.getReservableRoomId(),
				target.getReservableRoom().getReservableRoomId())) {
			//roomIdが違った時点で予約可能なのでfalseを返す
			return false;
		}

		//roomIdが同じなら、時間帯で判定しないといけない
		//予約時間が重複していないかを判定
		if (this.getStartTime().equals(target.getStartTime()) && this.getEndTime().equals(target.getEndTime())) {
			return true;
		}

		return target.getEndTime().isAfter(this.getStartTime()) && this.getEndTime().isAfter(target.getStartTime());
	}

	public Integer getReservationId() {
		return reservationId;
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
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
