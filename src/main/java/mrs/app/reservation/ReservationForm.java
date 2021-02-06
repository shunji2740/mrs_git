package mrs.app.reservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
@EndTimeMustBeAfterStartTime(message = "終了時刻は開始時刻より後にしてください")
@StartTimeMustBeAfterPresent(message = "指定時刻の予約は無効です")
public class ReservationForm implements Serializable {

	private Integer totalPrice;

	private UUID reservationIdForTimer;

	@ThirtyMinutesUnit(message = "30分単位で入力してください")
	@NotNull(message = "開始時間を指定してください")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@ThirtyMinutesUnit(message = "30分単位で入力してください")
	@NotNull(message = "終了時間を指定してください")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime endTime;

	@NotNull(message = "予約日を指定してください")
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate date;

	//通知メールのチェック値
	@Pattern(regexp="checked")
	private String notificationMailCheck;

	//選択されたお支払方法
	@NotNull(message = "お支払方法を指定してください")
	private String selectedPaymentMethod;

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

	public LocalDate getDate() {
		return date;
	}

	public String getNotificationMailCheck() {
		return notificationMailCheck;
	}

	public void setNotificationMailCheck(String notificationMailCheck) {
		this.notificationMailCheck = notificationMailCheck;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public String getSelectedPaymentMethod() {
		return selectedPaymentMethod;
	}

	public void setSelectedPaymentMethod(String selectedPaymentMethod) {
		this.selectedPaymentMethod = selectedPaymentMethod;
	}

	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public UUID getReservationIdForTimer() {
		return reservationIdForTimer;
	}

	public void setReservationIdForTimer(UUID reservationIdForTimer) {
		this.reservationIdForTimer = reservationIdForTimer;
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

}
