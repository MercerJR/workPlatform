package com.project.workplatform.annotion;

import com.project.workplatform.annotion.validation.MailValidation;
import com.project.workplatform.data.ValidConstant;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @Author: Mercer JR
 * @Date: 2022/3/4 19:46
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MailValidation.class})
public @interface IsMail {
    String message() default ValidConstant.STUDIO_CONTACT_MAIL_WRONG_FORMAT;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
