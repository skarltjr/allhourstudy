package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.event.form.EventForm;
import com.allhour.allhourstudy.modules.event.validator.EventValidator;
import com.allhour.allhourstudy.modules.study.Study;
import com.allhour.allhourstudy.modules.study.StudyRepository;
import com.allhour.allhourstudy.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
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
    private final StudyRepository studyRepository;
    private final EventRepository eventRepository;

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
        Event event = eventRepository.findById(id).orElseThrow();
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


        //todo  사진이미지 길이가 너무길어서 처리가안된다.
        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
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
}
