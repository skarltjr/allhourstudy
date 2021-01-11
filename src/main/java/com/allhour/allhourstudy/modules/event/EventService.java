package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.event.form.EventForm;
import com.allhour.allhourstudy.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;

    public Event createNewEvent(EventForm eventForm, Study study, Account account) {
        Event event = modelMapper.map(eventForm, Event.class);
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public void updateEvent(EventForm form, Event event) {
        modelMapper.map(form, event);
    }
}
