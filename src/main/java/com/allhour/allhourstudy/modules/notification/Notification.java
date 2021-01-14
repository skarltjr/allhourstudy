package com.allhour.allhourstudy.modules.notification;

import com.allhour.allhourstudy.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedEntityGraph(name = "Notification.withAll", attributeNodes = {
        @NamedAttributeNode("account")
})
@Entity @Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String message;

    private String link;

    private boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime createdLocalDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

}
/** 알림을 어떻게 설정해나갈것인가. - 비동기적 - 기존의 로직을 건드리지않은채로 Async하게 +
 *  모든 요청에 개입해야한다. Interceptor- post로 뷰에 렌더링하기전에
 *  ApplicationEventPublisher를 통해 정해진 이벤트를 publish하고 리스너를 통해 처리
 *  ex) 알림설정이 필요한 스터디개설 service단의 createNewStudy에서
 *  ->  eventPublisher.publishEvent(new StudyCreatedEvent(study));
 *  -> 새로운 StudyCreatedEvent클래스를 만들어주고
 *  -> 이를 처리할 리스너를만들어준다.
 *
 *  즉 1. 어떤 과정으로 알림을 보낼것인가(eventListener) -> 2. 어떻게 알림을 보낼것인가(Async) 순으로 코드를짜나가자.
 * */