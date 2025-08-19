package com.videoclub.filmoapp.core.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValidImpl.class)
public @interface EnumValid {

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String message() default "invalid value";

  @NotNull
  Class<? extends Enum<?>> target();

  boolean required() default true;
}
