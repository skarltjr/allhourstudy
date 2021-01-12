package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.event.form.EventForm;
import com.allhour.allhourstudy.modules.event.validator.EventValidator;
import com.allhour.allhourstudy.modules.study.Study;
import com.allhour.allhourstudy.modules.study.StudyRepository;
import com.allhour.allhourstudy.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class EventController {

    private final StudyService studyService;
    private final EventValidator eventValidator;
    private final EventService eventService;
    private final EnrollmentRepository enrollmentRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EnrollmentService enrollmentService;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    @GetMapping("/new-event")
    public String createEventForm(@CurrentUser Account account, @PathVariable String path, Model model) {

        model.addAttribute("account", account);
        Study study = studyService.getStudyToUpdateStatus(path, account);
        model.addAttribute("study", study);
        model.addAttribute("eventForm", new EventForm());
        return "event/form";
    }

    @PostMapping("/new-event")
    public String createEvent(@CurrentUser Account account, @PathVariable String path,
                              @ModelAttribute @Valid EventForm eventForm, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            return "event/form";
        }
        Event event = eventService.createNewEvent(eventForm, study, account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) +
                "/events/" + event.getId();
    }

    @GetMapping("/events/{id}")
    public String eventView(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                            Model model) {
        Study study = studyService.getStudy(path);
        Event event = eventRepository.findWithAllById(id);
        model.addAttribute("study", study);
        model.addAttribute("account", account);
        model.addAttribute("event", event);
        return "event/view";
    }

    @GetMapping("/events")
    public String studyEvents(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        /** A large object can be stored in several records, that's why you have to use a transaction.
         * All records are correct or nothing at all.
         *
         * EventRepoistory에 트랜잭션을 걸어주지 않아서 psql error -> 용량이 큰 객체를 오토커밋하지못함     */
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();
        for (Event e : events) {
            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(e);
            } else {
                newEvents.add(e);
            }
        }

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);
        return "study/events";
    }

    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                  Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        Event event = eventRepository.findWithAllById(id);
        model.addAttribute("event", event);
        EventForm eventForm = modelMapper.map(event, EventForm.class);
        model.addAttribute("eventForm", eventForm);
        return "event/update-form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                              @ModelAttribute @Valid EventForm form, Errors errors, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findWithAllById(id);

        eventValidator.updateCheck(form, event, errors);
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("event", event);
            return "event/update-form";
        }
        form.setEventType(event.getEventType());
        eventService.updateEvent(form, event);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Event event = eventRepository.findWithAllById(id);
        eventService.deleteEvent(event);
        Study study = studyService.getStudyToUpdate(path, account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events";
    }

    @PostMapping("/events/{id}/enroll")
    public String enrollEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        Event event = eventRepository.findWithAllById(id);
        eventService.enroll(event, account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
    }

    @PostMapping("/events/{id}/disEnroll")
    public String disEnrollEvent(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        Event event = eventRepository.findWithAllById(id);
        eventService.disEnroll(event, account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
    }
    //todo check 테스트account추가 후 로직 제대로 동작하는지 확인할 것


    @GetMapping("/events/{id}/enrollments/{enrollmentId}/accept")
    public String acceptEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                   @PathVariable Long enrollmentId) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findWithAllById(id);
        Enrollment enrollment = enrollmentRepository.findWithAllById(enrollmentId);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();

    }

    @GetMapping("/events/{id}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                                   @PathVariable Long enrollmentId) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findWithAllById(id);
        Enrollment enrollment = enrollmentRepository.findWithAllById(enrollmentId);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}/enrollments/{enrollmentId}/checkin")
    public String checkIn(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                          @PathVariable Long enrollmentId) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findWithAllById(id);
        Enrollment enrollment = enrollmentRepository.findWithAllById(enrollmentId);
        enrollmentService.checkIn(enrollment);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
    }

    @GetMapping("/events/{id}/enrollments/{enrollmentId}/noneCheckIn")
    public String noneCheckIn(@CurrentUser Account account, @PathVariable String path, @PathVariable Long id,
                          @PathVariable Long enrollmentId) {
        Study study = studyService.getStudyToUpdate(path, account);
        Event event = eventRepository.findWithAllById(id);
        Enrollment enrollment = enrollmentRepository.findWithAllById(enrollmentId);
        enrollmentService.noneCheckIn(enrollment);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
    }
}
