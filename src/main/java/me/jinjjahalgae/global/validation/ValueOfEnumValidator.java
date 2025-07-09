package me.jinjjahalgae.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValueOfEnumValidator implements ConstraintValidator<EnumValue, String> {

    private EnumValue enumValue;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValue = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 값 처리
        if (value == null) {
            return false;
        }
        
        // 빈 문자열이면 false
        if (value.trim().isEmpty()) {
            return false;
        }
        
         // enum 값 가져오기
        Enum<?>[] enumValues = this.enumValue.enumClass().getEnumConstants();
        
        if (enumValues != null) {
            String upperCaseValue = value.trim().toUpperCase();
            
            for (Object enumValue : enumValues) {
                if (upperCaseValue.equals(enumValue.toString().toUpperCase())) {
                    return true;
                }
            }
        }
         
        return false;
    }
    
}
