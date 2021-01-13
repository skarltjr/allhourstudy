package com.allhour.allhourstudy.modules.study.event;

import com.allhour.allhourstudy.infra.config.AppProperties;
import com.allhour.allhourstudy.infra.mail.EmailMessage;
import com.allhour.allhourstudy.infra.mail.EmailService;
import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.AccountRepository;
import com.allhour.allhourstudy.modules.notification.Notification;
import com.allhour.allhourstudy.modules.notification.NotificationRepository;
import com.allhour.allhourstudy.modules.notification.NotificationType;
import com.allhour.allhourstudy.modules.study.Study;
import com.allhour.allhourstudy.modules.study.StudyRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Async
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    /**  목적 : 알림을 보내는 것 .  - 비동기적으로 알림을 !보내는것 ! 자체가 목표
     * - 알림을 보내는것과 인터셉터로 알림아이콘이 변경되는 것은 별개 */

    //todo interceptor 알림 아이콘 변경을 위한 인터셉터
    //todo Event와 관련된event도 처리

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final EmailService emailService;

    // eventPublisher.publishEvent(new StudyCreatedEvent(study)); - eventPublisher가 publish한 이벤트 처리
    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        //생성된 스터디의 태그와 존을 갖고있는 모든 account에게 알림을 보내도록 하자
        Study study = studyRepository.findWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        List<Account> accounts = accountRepository.findAccounts(study.getTags(), study.getZones());
        for (Account account : accounts) {
            if (account.isStudyCreatedByEmail()) {
                // 메일보내기
                sendMailAboutStudy(study,account,"Check New Study");
                log.info("log for sending mail : StudyCreatedMail to "+account.getNickname() );
            }
            if (account.isStudyCreatedByWeb()) {
                //새로운 알림객체를 만들고 저장
                makeNotificationAboutStudy(study, account, study.getShortDescription(), NotificationType.STUDY_CREATED);
            }
        }


    }

    private void sendMailAboutStudy(Study study, Account account, String contextMessage) {
        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("link", "/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8));
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);


        EmailMessage emailMessage = EmailMessage.builder()
                .subject(contextMessage)
                .to(account.getEmail())
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);
    }

    private void makeNotificationAboutStudy(Study study, Account account, String shortDescription,NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setTitle(study.getTitle());
        notification.setMessage(shortDescription);
        notification.setChecked(false);
        notification.setNotificationType(notificationType);
        notification.setLink("/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8));
        notification.setCreatedLocalDateTime(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        /** 타켓 : 스터디와 관련된 매니저와 멤버*/
        Study study = studyRepository.findWithAllMemberByPath(studyUpdateEvent.getStudy().getPath());
        Set<Account> allMembers = new HashSet<>();
        allMembers.addAll(study.getMembers());
        allMembers.addAll(study.getManagers());
        for (Account member : allMembers) {
            if (member.isStudyUpdatedByEmail()) {
                sendMailAboutStudy(study,member,"스터디가 업데이트 되었습니다.");
                log.info("log for sending mail : StudyUpdatedMail to "+member.getNickname() );
            }
            if (member.isStudyUpdatedByWeb()) {
                makeNotificationAboutStudy(study,member,study.getShortDescription(),NotificationType.STUDY_UPDATED);
            }
        }
    }
}
