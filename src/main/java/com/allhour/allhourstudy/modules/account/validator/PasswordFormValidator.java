package com.allhour.allhourstudy.modules.account.validator;

import com.allhour.allhourstudy.modules.account.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PasswordForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm password = (PasswordForm) target;
        if (!password.getNewPassword().equals(password.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword","wrong.value","패스워드가 일치하지 않습니다" +
                    "");
        }
    }
}
