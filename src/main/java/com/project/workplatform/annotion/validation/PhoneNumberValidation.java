package com.project.workplatform.annotion.validation;

import com.project.workplatform.annotion.IsPhoneNumber;
import com.project.workplatform.util.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/12 20:05
 */
public class PhoneNumberValidation implements ConstraintValidator<IsPhoneNumber,String> {
    @Override
    public void initialize(IsPhoneNumber constraintAnnotation) {

    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return ValidUtil.checkPhoneNumber(phoneNumber);
    }
}
