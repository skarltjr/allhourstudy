package com.allhour.allhourstudy.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void changeAsRead(List<Notification> notifications) {
        for (Notification notification : notifications) {
            notification.setChecked(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void deleteOld(List<Notification> lists) {
        notificationRepository.deleteAll(lists);
    }
}
