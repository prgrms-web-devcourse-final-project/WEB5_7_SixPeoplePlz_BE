package me.jinjjahalgae.global.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, 
    ElementType.PARAMETER, ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueOfEnumValidator.class)
public @interface EnumValue {
    String message() default "유효하지 않은 enum 값입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

     //검증할 enum 클래스
    Class<? extends Enum<?>> enumClass();

    //null 값을 허용할지 여부
    boolean nullable() default false;

    //문자열 비교 시 공백 제거 여부
    boolean trim() default true;
}
