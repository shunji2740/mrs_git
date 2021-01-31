package mrs.app.reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndTimeMustBeAfterStartTimeValidator
		implements ConstraintValidator<EndTimeMustBeAfterStartTime, ReservationForm> {

	private String message;

	@Override
	public void initialize(EndTimeMustBeAfterStartTime constraintAnnotation) {
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {


		//入力値がnullだった場合は@NotNullに委譲する
		if (value.getStartTime() == null || value.getEndTime() == null) {
			return true;
		}

		//終了時間が開始時間より後にきていないかをチェック
		boolean isEndTimeMustBeAfterStartTime = value.getEndTime().isAfter(value.getStartTime());

		//終了時間が開始時間より後にきていた場合の処理
		if (!isEndTimeMustBeAfterStartTime) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message).addPropertyNode("endTime").addConstraintViolation();
		}

		return isEndTimeMustBeAfterStartTime;
	}
}
