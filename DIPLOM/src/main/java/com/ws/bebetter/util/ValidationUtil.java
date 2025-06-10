package com.ws.bebetter.util;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;

public class ValidationUtil {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = StringValidator.class)
    public @interface ValidString {
        String message() default "Недопустимое значение";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        Type type();

        int min() default 0;

        int max() default Integer.MAX_VALUE;

        enum Type {PASSWORD, EMAIL, UUID}
    }

    @Constraint(validatedBy = EnumValidator.class)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EnumConstraint {
        String message() default "Значение не соответствует допустимым значениям перечисления";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        Class<? extends Enum<?>> enumClass();
    }

    public static class StringValidator implements ConstraintValidator<ValidString, String> {

        private static final Pattern PASSWORD_PATTERN =
                Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!\"#$%^&*()\\-+=?/\\\\]).{8,50}$");
        private static final Pattern EMAIL_PATTERN =
                Pattern.compile("^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$");
        private ValidString.Type type;
        private int min;
        private int max;

        @Override
        public void initialize(ValidString constraintAnnotation) {
            this.type = constraintAnnotation.type();

            this.min = constraintAnnotation.min();

            this.max = constraintAnnotation.max();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
            if (value == null || value.length() > max || value.length() < min) {
                return false;
            }

            return switch (type) {
                case PASSWORD -> PASSWORD_PATTERN.matcher(value).matches();
                case EMAIL -> EMAIL_PATTERN.matcher(value).matches();
                case UUID -> isValidUUID(value);
            };
        }

        private boolean isValidUUID(String value) {
            try {
                UUID.fromString(value);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

    }

    public static class EnumValidator implements ConstraintValidator<ValidationUtil.EnumConstraint, Enum<?>> {

        private Class<? extends Enum<?>> enumClass;

        @Override
        public void initialize(ValidationUtil.EnumConstraint annotation) {
            this.enumClass = annotation.enumClass();
        }

        @Override
        public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            return Arrays.asList(enumClass.getEnumConstants()).contains(value);
        }
    }
}

