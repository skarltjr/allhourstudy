package com.allhour.allhourstudy.modules.study.validator;


import com.allhour.allhourstudy.modules.study.StudyRepository;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class StudyFormValidator implements Validator {

    private final StudyRepository studyRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return StudyForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudyForm form = (StudyForm) target;
        if (studyRepository.existsByPath(form.getPath()))
        {
            errors.rejectValue("path","wrong.path","해당 스터디 path를 사용할 수 없습니다.");
        }
    }
}
