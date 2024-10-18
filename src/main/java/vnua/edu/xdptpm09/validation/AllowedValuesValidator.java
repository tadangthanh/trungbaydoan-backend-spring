package vnua.edu.xdptpm09.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {
    private String[] values;

    public AllowedValuesValidator() {
    }

    public void initialize(AllowedValues constraint) {
        this.values = constraint.anyOf();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(this.values).contains(value.toUpperCase());
    }
}
