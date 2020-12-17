package mrs.app.reservation;

import java.time.LocalTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ThirtyMinutesUnitValidator implements ConstraintValidator<ThirtyMinutesUnit, LocalTime>{

	@Override
	public boolean isValid(LocalTime value, ConstraintValidatorContext context) {

		return false;
	}

}
