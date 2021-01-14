package com.allhour.allhourstudy.modules.notification;

import com.allhour.allhourstudy.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NotificationRepository extends JpaRepository<Notification,Long> {

    int countByAccountAndChecked(Account account, boolean b);

    @EntityGraph(value = "Notification.withAll",type = EntityGraph.EntityGraphType.FETCH)
    List<Notification> findByAccountAndCheckedOrderByCreatedLocalDateTimeDesc(Account account, boolean b);

    @Transactional
    void deleteByAccountAndChecked(Account account, boolean b);
}

