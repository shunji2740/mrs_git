
package mrs.domain.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

//予約情報を格納するテーブル
@Entity
public class Reservation extends ReservationBaseEntity implements Serializable {

	private Integer totalPrice;

	private UUID reservationIdForTimer;

	//追加備品(配列型)
	@Type(type = "string-array")
	@Column(name = "additional_equipments", columnDefinition = "text[]")
	private String[] additionalEquipment;

	//ケータリング
	@Type(type = "int-array")
	@Column(name = "catering", columnDefinition = "int[]")
	private int[] catering;



	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public UUID getReservationIdForTimer() {
		return reservationIdForTimer;
	}

	public void setReservationIdForTimer(UUID uuid) {
		this.reservationIdForTimer = uuid;
	}

	public String[] getAdditionalEquipments() {
		return additionalEquipment;
	}

	public void setAdditionalEquipments(String[] additionalEquipments) {
		this.additionalEquipment = additionalEquipments;
	}

	public int[] getCatering() {
		return catering;
	}

	public void setCatering(int[] catering) {
		this.catering = catering;
	}

}
