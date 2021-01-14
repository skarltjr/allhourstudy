package com.allhour.allhourstudy.modules.notification;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;


    @GetMapping("/notifications")
    public String notifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, false);
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();
        for (Notification noti : notifications) {
            if (noti.getNotificationType() == NotificationType.STUDY_CREATED) {
                newStudyNotifications.add(noti);
            }
            if (noti.getNotificationType() == NotificationType.STUDY_UPDATED) {
                watchingStudyNotifications.add(noti);
            }
            if (noti.getNotificationType() == NotificationType.EVENT_ENROLLMENT) {
                eventEnrollmentNotifications.add(noti);
            }
        }
        int count = notificationRepository.countByAccountAndChecked(account, true);
        model.addAttribute("isNew", true);
        model.addAttribute("account", account);
        model.addAttribute("numberOfChecked", count);
        model.addAttribute("notifications", notifications);
        model.addAttribute("numberOfNotChecked", notifications.size());
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        notificationService.changeAsRead(notifications);
        return "notifications/list";
    }

    @GetMapping("/notifications/old")
    public String viewOldNotifications(@CurrentUser Account account, Model model) {
        List<Notification> notifications = notificationRepository.findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(account, true);
        List<Notification> newStudyNotifications = new ArrayList<>();
        List<Notification> eventEnrollmentNotifications = new ArrayList<>();
        List<Notification> watchingStudyNotifications = new ArrayList<>();
        for (Notification noti : notifications) {
            if (noti.getNotificationType() == NotificationType.STUDY_CREATED) {
                newStudyNotifications.add(noti);
            }
            if (noti.getNotificationType() == NotificationType.STUDY_UPDATED) {
                watchingStudyNotifications.add(noti);
            }
            if (noti.getNotificationType() == NotificationType.EVENT_ENROLLMENT) {
                eventEnrollmentNotifications.add(noti);
            }
        }
        int count = notificationRepository.countByAccountAndChecked(account, false);
        model.addAttribute("isNew", false);
        model.addAttribute("account", account);
        model.addAttribute("numberOfNotChecked", count);
        model.addAttribute("notifications", notifications);
        model.addAttribute("numberOfChecked", notifications.size());
        model.addAttribute("newStudyNotifications", newStudyNotifications);
        model.addAttribute("watchingStudyNotifications", watchingStudyNotifications);
        model.addAttribute("eventEnrollmentNotifications", eventEnrollmentNotifications);
        return "notifications/list";
    }

    @PostMapping("/notifications/delete")
    public String deleteOlfNotifications(@CurrentUser Account account) {
        notificationRepository.deleteByAccountAndChecked(account, true);
        return "redirect:/notifications";
    }
}
