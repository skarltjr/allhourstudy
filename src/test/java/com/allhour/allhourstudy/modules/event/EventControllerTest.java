package com.allhour.allhourstudy.modules.event;


import com.allhour.allhourstudy.modules.account.*;
import com.allhour.allhourstudy.modules.study.Study;
import com.allhour.allhourstudy.modules.study.StudyFactory;
import com.allhour.allhourstudy.modules.study.StudyRepository;
import com.allhour.allhourstudy.modules.study.StudyService;
import com.allhour.allhourstudy.modules.tag.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE_TIME;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EventService eventService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    StudyService studyService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    StudyFactory studyFactory;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    EventRepository eventRepository;


    @AfterEach
    public void afterEach() {
        if (enrollmentRepository.count() > 0) {
            enrollmentRepository.deleteAll();
        }
        if (eventRepository.count() > 0) {
            eventRepository.deleteAll();
        }
        if (studyRepository.count() > 0) {
            studyRepository.deleteAll();
        }
        //계정먼저지우면 스터디를 못지움
        if (accountRepository.count() > 0) {
            accountRepository.deleteAll();
        }
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setTitle(eventTitle);
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setStudy(study);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(6));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        event.setCreatedBy(account);
        return eventRepository.save(event);
    }

    @Test
    @DisplayName("모임 생성 뷰")
    @WithAccount("kiseok")
    void createEventForm() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        studyFactory.createStudy("test-path", kiseok);
        mockMvc.perform(get("/study/test-path/new-event"))
                .andExpect(model().attributeExists("eventForm"))
                .andExpect(view().name("event/form"));

    }
  /*  @Test
    @DisplayName("모임 생성")
    @WithAccount("kiseok")
    void createEvent()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);

        mockMvc.perform(post("/study/test-path/new-event")
                .with(csrf())
                .param("title", "test-event")
                .param("description", "소개입니다")
                .param("limitOfEnrollments", "2")
                .param("endEnrollmentDateTime",LocalDateTime.now().plusDays(1).toString())
                .param("startDateTime", LocalDateTime.of(2021, 2, 2, 1, 1).toString())
                .param("endDateTime", LocalDateTime.of(2021, 3, 2, 1, 1).toString()))
                .andExpect(status().is3xxRedirection());

        Event byTitle = eventRepository.findByTitle("test-event");
        assertNotNull(byTitle);
    }*/


    @Test
    @DisplayName("선착순 모임에 참가 - 자동수락")
    @WithAccount("kiseok")
    void newEnrollment_to_FCFS_event_accepted()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, kiseok);

        mockMvc.perform(post("/study/test-path/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Account newAccount = accountRepository.findByNickname("kiseok");
        Enrollment enroll = enrollmentRepository.findByEventAndAccount(event, newAccount);
        assertTrue(enroll.isAccepted());
    }
    @Test
    @DisplayName("선착순 모임에 참가 - 인원full- 대기")
    @WithAccount("kiseok")
    void newEnrollment_to_FCFS_event_not_accepted()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, kiseok);

        Account friend1 = accountFactory.createAccount("friend1");
        Account friend2 = accountFactory.createAccount("friend2");
        eventService.enroll(event, friend1);
        eventService.enroll(event, friend2);

        mockMvc.perform(post("/study/test-path/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        Account newAccount = accountRepository.findByNickname("kiseok");
        Enrollment enroll = enrollmentRepository.findByEventAndAccount(event, newAccount);
        assertFalse(enroll.isAccepted());
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("kiseok")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, kiseok);

        eventService.enroll(event, kiseok);
        Account friend1 = accountFactory.createAccount("friend1");
        Account friend2 = accountFactory.createAccount("friend2");
        eventService.enroll(event, friend1);
        eventService.enroll(event, friend2);
        mockMvc.perform(post("/study/test-path/events/" + event.getId() + "/disEnroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Enrollment enroll = enrollmentRepository.findByEventAndAccount(event, friend2);
        assertTrue(enroll.isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("kiseok")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Account friend1 = accountFactory.createAccount("friend1");
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, friend1);

        mockMvc.perform(post("/study/test-path/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        Enrollment ki = enrollmentRepository.findByEventAndAccount(event, kiseok);
        assertFalse(ki.isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 수락")
    @WithAccount("kiseok")
    void newEnrollment_Accept()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Account friend1 = accountFactory.createAccount("friend1");
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, kiseok);

        eventService.enroll(event,friend1);
        Enrollment enroll = enrollmentRepository.findByEventAndAccount(event, friend1);
        mockMvc.perform(get("/study/test-path/events/" + event.getId() + "/enrollments/"+enroll.getId()+"/accept"))
                .andExpect(status().is3xxRedirection());

        assertTrue(enroll.isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 거절")
    @WithAccount("kiseok")
    void newEnrollment_Reject()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Account friend1 = accountFactory.createAccount("friend1");
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, kiseok);

        eventService.enroll(event,friend1);
        Enrollment enroll = enrollmentRepository.findByEventAndAccount(event, friend1);
        mockMvc.perform(get("/study/test-path/events/" + event.getId() + "/enrollments/"+enroll.getId()+"/reject"))
                .andExpect(status().is3xxRedirection());

        assertFalse(enroll.isAccepted());
    }
}