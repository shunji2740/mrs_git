package mrs.app.reservation;

import java.time.LocalDateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartTimeMustBeAfterPresentValidator
		implements ConstraintValidator<StartTimeMustBeAfterPresent, ReservationForm> {

	private String message;

	@Override
	public void initialize(StartTimeMustBeAfterPresent constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {

		//入力値がnullだった場合は@NotNullに委譲する
		if(value.getDate() == null) {
			return true;
		}

		//入力値がnullだった場合は@NotNullに委譲する
		if (value.getStartTime() == null || value.getEndTime() == null) {
			return true;
		}

		LocalDateTime localDateTimeOfReservation = LocalDateTime.of(value.getDate(), value.getStartTime());
		boolean blStartTimeMustBeAfterPresent = localDateTimeOfReservation.isAfter(LocalDateTime.now());

		//終了時間が開始時間より後にきていた場合の処理
		if (!blStartTimeMustBeAfterPresent) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message).addPropertyNode("endTime").addConstraintViolation();
		}

		return blStartTimeMustBeAfterPresent;
	}
}
