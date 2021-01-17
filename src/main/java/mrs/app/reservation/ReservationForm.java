package mrs.app.reservation;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

@EndTimeMustBeAfterStartTime(message = "終了時刻は開始時刻より後にしてください")
public class ReservationForm implements Serializable {
	@ThirtyMinutesUnit(message = "30分単位で入力してください")
	@NotNull(message = "必須です")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime startTime;

	@ThirtyMinutesUnit(message = "30分単位で入力してください")
	@NotNull(message = "必須です")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime endTime;

	@NotNull(message = "必須です")
	@DateTimeFormat(pattern = "yyyy/MM/dd")
	private LocalDate date;

	@Pattern(regexp="checked")
	private String inputSingleCheck;


	public LocalDate getDate() {
		return date;
	}

	public String getInputSingleCheck() {
		return inputSingleCheck;
	}

	public void setInputSingleCheck(String inputSingleCheck) {
		this.inputSingleCheck = inputSingleCheck;
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

}
