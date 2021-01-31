
package mrs.domain.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

//予約情報を格納するテーブル
@Entity
public class Reservation extends ReservationBaseEntity implements Serializable {

	private Integer totalPrice;

	private UUID reservationIdForTimer;

	//追加備品
	@Type(type = "list-array")
	@Column(name = "additional_equipments", columnDefinition = "text[]")
	private List<String> additionalEquipments;

	//ケータリング数量
	@Type(type = "int-array")
	@Column(name = "catering", columnDefinition = "int[]")
	private int[] cateringQuantity;

	//選択されたケータリングを格納
	@Type(type = "list-array")
	@Column(name = "catering_selection", columnDefinition = "text[]")
	private List<String> cateringSelection;

	//選択されたお支払方法
	private String selectedPaymentMethod;

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

	public List<String> getAdditionalEquipments() {
		return additionalEquipments;
	}

	public void setAdditionalEquipments(List<String> additionalEquipments) {
		this.additionalEquipments = additionalEquipments;
	}

	public int[] getCateringQuantity() {
		return cateringQuantity;
	}

	public void setCateringQuantity(int[] cateringQuantity) {
		this.cateringQuantity = cateringQuantity;
	}

	public List<String> getCateringSelection() {
		return cateringSelection;
	}

	public void setCateringSelection(List<String> cateringSelection) {
		this.cateringSelection = cateringSelection;
	}

	public String getSelectedPaymentMethod() {
		return selectedPaymentMethod;
	}

	public void setSelectedPaymentMethod(String selectedPaymentMethod) {
		this.selectedPaymentMethod = selectedPaymentMethod;
	}



}
