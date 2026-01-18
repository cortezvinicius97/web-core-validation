package com.vcinsidedigital.webcore.validation.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<FieldError> errors;

    public ValidationException(List<FieldError> errors) {
        super(buildMessage(errors));
        this.errors = errors;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    private static String buildMessage(List<FieldError> errors) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        for (int i = 0; i < errors.size(); i++) {
            FieldError error = errors.get(i);
            sb.append(error.getField()).append(" - ").append(error.getMessage());
            if (i < errors.size() - 1) {
                sb.append("; ");
            }
        }
        return sb.toString();
    }

    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    ", rejectedValue=" + rejectedValue +
                    '}';
        }
    }
}
