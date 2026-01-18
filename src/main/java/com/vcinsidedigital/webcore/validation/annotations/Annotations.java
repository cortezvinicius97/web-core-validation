package com.vcinsidedigital.webcore.validation.annotations;

import java.lang.annotation.*;


public class Annotations {
    // NotNull
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotNull {
        String message() default "Field cannot be null";
    }

    // NotEmpty
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotEmpty {
        String message() default "Field cannot be empty";
    }

    // NotBlank
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotBlank {
        String message() default "Field cannot be blank";
    }

    // Size
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Size {
        int min() default 0;
        int max() default Integer.MAX_VALUE;
        String message() default "Field size is invalid";
    }

    // Min
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Min {
        long value();
        String message() default "Value must be greater than or equal to {value}";
    }

    // Max
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Max {
        long value();
        String message() default "Value must be less than or equal to {value}";
    }

    // Email
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Email {
        String message() default "Invalid email format";
    }

    // Pattern
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Pattern {
        String regexp();
        String message() default "Field does not match the required pattern";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Valid {

    }
}




