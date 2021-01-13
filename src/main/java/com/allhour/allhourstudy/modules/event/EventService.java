package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.event.event.EnrollmentAcceptEvent;
import com.allhour.allhourstudy.modules.event.event.EnrollmentRejectEvent;
import com.allhour.allhourstudy.modules.event.form.EventForm;
import com.allhour.allhourstudy.modules.study.Study;
import com.allhour.allhourstudy.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;


    public Event createNewEvent(EventForm eventForm, Study study, Account account) {
        Event event = modelMapper.map(eventForm, Event.class);
        event.setStudy(study);
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new StudyUpdateEvent(study,event.getTitle()+" 모임이 추가되었습니다."));
        return eventRepository.save(event);
    }

    public void updateEvent(EventForm form, Event event) {
        modelMapper.map(form, event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),event.getTitle()+"모임이 변경되었습니다."));
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),event.getTitle()+"모임이 취소되었습니다."));
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

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        if (event.getEventType() == EventType.CONFIRMATIVE && event.numberOfRemainSpots() >= 1) {
            enrollment.setAccepted(true);
            eventPublisher.publishEvent(new EnrollmentAcceptEvent(enrollment));
        }
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        if (event.getEventType() == EventType.CONFIRMATIVE) {
            enrollment.setAccepted(false);
            eventPublisher.publishEvent(new EnrollmentRejectEvent(enrollment));
        }
    }

}
