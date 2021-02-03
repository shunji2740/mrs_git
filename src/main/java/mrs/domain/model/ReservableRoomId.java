package mrs.domain.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.sun.istack.NotNull;

//複合クラス
@Embeddable
public class ReservableRoomId implements Serializable{

	private Integer roomId;

	@Column(name = "reserved_date", columnDefinition = "DATE")
    @NotNull
	private LocalDate reservedDate;

	public ReservableRoomId(Integer roomId, LocalDate reservedDate) {
		this.roomId = roomId;
		this.reservedDate = reservedDate;
	}

	public ReservableRoomId() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reservedDate == null)? 0 : reservedDate.hashCode());
		result = prime * result + ((roomId == null)? 0 : roomId.hashCode());

		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if(this == obj) return true;
		if(obj == null) return false;

		ReservableRoomId other = (ReservableRoomId) obj;

		if(getClass() != obj.getClass()) return false;


		if(reservedDate == null) {
			if(other.reservedDate != null) return false;
		}

		if(!reservedDate.equals(other.reservedDate)) return false;

		if(roomId == null) {
			if(other.roomId != null) return false;
		}

		if(!roomId.equals(other.roomId)) return false;


		return true;
	}


	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public LocalDate getReservedDate() {
		return reservedDate;
	}

	public void setReservedDate(LocalDate reservedDate) {
		this.reservedDate = reservedDate;
	}

}
