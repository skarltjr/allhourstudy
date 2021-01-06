package com.allhour.allhourstudy.modules.account.validator;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.AccountRepository;
import com.allhour.allhourstudy.modules.account.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return NicknameForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm form = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(form.getNickname());
        if (byNickname != null) {
            errors.rejectValue("nickname","wrong.value","사용할 수 없는 닉네임입니다");
        }
    }
}
