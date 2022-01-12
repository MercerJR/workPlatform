package com.project.workplatform.annotion;

import com.project.workplatform.annotion.validation.PhoneNumberValidation;
import com.project.workplatform.data.ValidConstant;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author: Mercer JR
 * @Date: 2022/1/12 20:10
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {PhoneNumberValidation.class})
public @interface IsPhoneNumber {
    String message() default ValidConstant.PHONE_NUMBER_WRONG_FORMAT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
