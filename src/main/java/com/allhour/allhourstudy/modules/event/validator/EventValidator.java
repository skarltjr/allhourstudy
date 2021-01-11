package com.allhour.allhourstudy.modules.event.validator;

import com.allhour.allhourstudy.modules.event.Event;
import com.allhour.allhourstudy.modules.event.form.EventForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return EventForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventForm eventForm = (EventForm) target;

        if (eventForm.getEndEnrollmentDateTime().isBefore(LocalDateTime.now())) {
            errors.rejectValue("endEnrollmentDateTime","wrong.datetime","모임 접수 종료시기가 올바르지 않습니다.");
        }
        if (!eventForm.getStartDateTime().isBefore(eventForm.getEndEnrollmentDateTime()) ||
                !eventForm.getStartDateTime().isBefore(eventForm.getEndDateTime())) {
            errors.rejectValue("startDateTime", "wrong.datetime", "모임 시작 일시를 다시 확인해주세요.");
        }
        if (!eventForm.getEndDateTime().isBefore(eventForm.getEndEnrollmentDateTime())) {
            errors.rejectValue("endDateTime","wrong.datetime","모임 시작 혹은 종료 시기를 다시 확인해주세요.");
        }
    }


    public void updateCheck(EventForm form, Event event, Errors errors) {
        if (event.getLimitOfEnrollments() > form.getLimitOfEnrollments()) {
            errors.rejectValue("limitOfEnrollments", "wrong.value", "확인된 참가 신청보다 모집 인원 수가 커야합니다.");
        }
    }
}
