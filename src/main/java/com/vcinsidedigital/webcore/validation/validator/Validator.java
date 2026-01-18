package com.vcinsidedigital.webcore.validation.validator;

import com.vcinsidedigital.webcore.validation.annotations.*;

import com.vcinsidedigital.webcore.validation.annotations.Annotations.*;
import com.vcinsidedigital.webcore.validation.exception.ValidationException;
import com.vcinsidedigital.webcore.validation.exception.ValidationException.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {

    public static void validate(Object object) throws ValidationException {
        if (object == null) {
            return;
        }

        List<FieldError> errors = new ArrayList<>();
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(object);

                // @NotNull
                if (field.isAnnotationPresent(NotNull.class)) {
                    validateNotNull(field, value, errors);
                }

                // @NotEmpty
                if (field.isAnnotationPresent(NotEmpty.class)) {
                    validateNotEmpty(field, value, errors);
                }

                // @NotBlank
                if (field.isAnnotationPresent(NotBlank.class)) {
                    validateNotBlank(field, value, errors);
                }

                // @Size
                if (field.isAnnotationPresent(Size.class)) {
                    validateSize(field, value, errors);
                }

                // @Min
                if (field.isAnnotationPresent(Min.class)) {
                    validateMin(field, value, errors);
                }

                // @Max
                if (field.isAnnotationPresent(Max.class)) {
                    validateMax(field, value, errors);
                }

                // @Email
                if (field.isAnnotationPresent(Email.class)) {
                    validateEmail(field, value, errors);
                }

                // @Pattern
                if (field.isAnnotationPresent(com.vcinsidedigital.webcore.validation.annotations.Annotations.Pattern.class)) {
                    validatePattern(field, value, errors);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field: " + field.getName(), e);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private static void validateNotNull(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            Annotations.NotNull annotation = field.getAnnotation(Annotations.NotNull.class);
            errors.add(new FieldError(field.getName(), annotation.message(), value));
        }
    }

    private static void validateNotEmpty(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            NotEmpty annotation = field.getAnnotation(NotEmpty.class);
            errors.add(new FieldError(field.getName(), annotation.message(), value));
            return;
        }

        if (value instanceof String && ((String) value).isEmpty()) {
            NotEmpty annotation = field.getAnnotation(NotEmpty.class);
            errors.add(new FieldError(field.getName(), annotation.message(), value));
        } else if (value instanceof java.util.Collection && ((java.util.Collection<?>) value).isEmpty()) {
            NotEmpty annotation = field.getAnnotation(NotEmpty.class);
            errors.add(new FieldError(field.getName(), annotation.message(), value));
        }
    }

    private static void validateNotBlank(Field field, Object value, List<FieldError> errors) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            NotBlank annotation = field.getAnnotation(NotBlank.class);
            errors.add(new FieldError(field.getName(), annotation.message(), value));
        }
    }

    private static void validateSize(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            return;
        }

        Size annotation = field.getAnnotation(Size.class);
        int size = 0;

        if (value instanceof String) {
            size = ((String) value).length();
        } else if (value instanceof java.util.Collection) {
            size = ((java.util.Collection<?>) value).size();
        } else if (value.getClass().isArray()) {
            size = java.lang.reflect.Array.getLength(value);
        }

        if (size < annotation.min() || size > annotation.max()) {
            String message = annotation.message()
                    .replace("{min}", String.valueOf(annotation.min()))
                    .replace("{max}", String.valueOf(annotation.max()));
            errors.add(new FieldError(field.getName(), message, value));
        }
    }

    private static void validateMin(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            return;
        }

        Min annotation = field.getAnnotation(Min.class);
        long numValue = 0;

        if (value instanceof Number) {
            numValue = ((Number) value).longValue();
        }

        if (numValue < annotation.value()) {
            String message = annotation.message().replace("{value}", String.valueOf(annotation.value()));
            errors.add(new FieldError(field.getName(), message, value));
        }
    }

    private static void validateMax(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            return;
        }

        Max annotation = field.getAnnotation(Max.class);
        long numValue = 0;

        if (value instanceof Number) {
            numValue = ((Number) value).longValue();
        }

        if (numValue > annotation.value()) {
            String message = annotation.message().replace("{value}", String.valueOf(annotation.value()));
            errors.add(new FieldError(field.getName(), message, value));
        }
    }

    private static void validateEmail(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            return;
        }

        if (value instanceof String) {
            String email = (String) value;
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

            if (!Pattern.compile(emailRegex).matcher(email).matches()) {
                Email annotation = field.getAnnotation(Email.class);
                errors.add(new FieldError(field.getName(), annotation.message(), value));
            }
        }
    }

    private static void validatePattern(Field field, Object value, List<FieldError> errors) {
        if (value == null) {
            return;
        }

        if (value instanceof String) {
            com.vcinsidedigital.webcore.validation.annotations.Annotations.Pattern annotation =
                    field.getAnnotation(com.vcinsidedigital.webcore.validation.annotations.Annotations.Pattern.class);
            String stringValue = (String) value;

            if (!Pattern.compile(annotation.regexp()).matcher(stringValue).matches()) {
                errors.add(new FieldError(field.getName(), annotation.message(), value));
            }
        }
    }
}
