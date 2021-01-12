package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.event.form.EventForm;
import com.allhour.allhourstudy.modules.study.Study;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final ModelMapper modelMapper;
    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;

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

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    public void enroll(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);

        if (enrollment == null) {
            Enrollment newEnrollment = new Enrollment();
            newEnrollment.setAccount(account);
            newEnrollment.setEnrolledAt(LocalDateTime.now());
            event.getEnrollments().add(newEnrollment);
            newEnrollment.setEvent(event);

            if (event.getEventType() == EventType.FCFS &&
                    event.getLimitOfEnrollments() > event.getEnrollments().stream().filter(Enrollment::isAccepted).count()) {
                newEnrollment.setAccepted(true);
            }
            enrollmentRepository.save(newEnrollment);
        }
    }

    public void disEnroll(Event event, Account account) {
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        if (enrollment.isAttended() == false) {
            event.getEnrollments().remove(enrollment);
            enrollment.setEvent(null);
            enrollmentRepository.delete(enrollment);

            //대기중인 등록 중 첫번째 자동확정시켜주기
            List<Enrollment> list = event.getEnrollments();
            Enrollment waitingOne = null;
            for (Enrollment waiting : list) {
                if (!waiting.isAccepted()) {
                    waitingOne = waiting;
                    break;
                }
            }
            if (waitingOne != null) {
                waitingOne.setAccepted(true);
            }

        }
    }
}
