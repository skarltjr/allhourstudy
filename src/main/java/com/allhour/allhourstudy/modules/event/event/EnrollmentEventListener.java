package com.allhour.allhourstudy.modules.event.event;

import com.allhour.allhourstudy.infra.config.AppProperties;
import com.allhour.allhourstudy.infra.mail.EmailMessage;
import com.allhour.allhourstudy.infra.mail.EmailService;
import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.event.Enrollment;
import com.allhour.allhourstudy.modules.event.EnrollmentRepository;
import com.allhour.allhourstudy.modules.event.Event;
import com.allhour.allhourstudy.modules.notification.Notification;
import com.allhour.allhourstudy.modules.notification.NotificationRepository;
import com.allhour.allhourstudy.modules.notification.NotificationType;
import com.allhour.allhourstudy.modules.study.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Async
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollmentEventListener {

    private final EnrollmentRepository enrollmentRepository;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollmentAcceptEvent(EnrollmentAcceptEvent enrollmentAcceptEvent)
    {
        Enrollment enrollment = enrollmentRepository.findWithAllById(enrollmentAcceptEvent.getEnrollment().getId());
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if (account.isStudyEnrollResultByEmail()) {
            sendAcceptedEmail(account, event, study, "Accepted - " + event.getTitle());
        }
        if (account.isStudyEnrollResultByWeb()) {
            makeEnrollmentNotification(account,event,study,"Accepted - " + event.getTitle());
        }
    }

    private void makeEnrollmentNotification(Account account, Event event, Study study, String text) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setTitle(study.getTitle() + " with "+event.getTitle());
        notification.setMessage(text);
        notification.setChecked(false);
        notification.setNotificationType(NotificationType.EVENT_ENROLLMENT);
        notification.setLink("/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8)+ "/events/" + event.getId());
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    private void sendAcceptedEmail(Account account, Event event, Study study, String text) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", text);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("All H our Study, " + event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    @EventListener
    public void handleEnrollmentRejectEvent(EnrollmentRejectEvent enrollmentRejectEvent)
    {
        Enrollment enrollment = enrollmentRepository.findWithAllById(enrollmentRejectEvent.getEnrollment().getId());
        Account account = enrollment.getAccount();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if (account.isStudyEnrollResultByEmail()) {
            sendAcceptedEmail(account, event, study, "Rejected - " + event.getTitle());
        }
        if (account.isStudyEnrollResultByWeb()) {
            makeEnrollmentNotification(account,event,study,"Rejected - " + event.getTitle());
        }
    }
}
