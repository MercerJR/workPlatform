package com.project.workplatform.annotion.validation;

import com.project.workplatform.annotion.IsMail;
import com.project.workplatform.annotion.IsPhoneNumber;
import com.project.workplatform.util.ValidUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/4 19:48
 */
public class MailValidation implements ConstraintValidator<IsMail,String> {
    @Override
    public void initialize(IsMail constraintAnnotation) {

    }

    @Override
    public boolean isValid(String mail, ConstraintValidatorContext constraintValidatorContext) {
        return ValidUtil.checkMail(mail);
    }
}
