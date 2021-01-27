package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.UserAccount;
import com.allhour.allhourstudy.modules.study.Study;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NamedEntityGraph(name = "Event.withEnrollments",attributeNodes = {
        @NamedAttributeNode("enrollments")
})
@NamedEntityGraph(name = "Event.withAll",attributeNodes = {
        @NamedAttributeNode("study"),
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("enrollments")
})
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {
    @Id @Column(name = "event_id")
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 이벤트 - 스터디는 거의 항상 같이 다닌다.
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime; //모임 시작

    @Column(nullable = false)
    private LocalDateTime endDateTime; // 모임 종료

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    private Integer limitOfEnrollments;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @OneToMany(mappedBy = "event")
    @OrderBy("enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>();

    public boolean isEnrollableFor(UserAccount userAccount) {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now()) &&
                !isAlreadyEnrolled(userAccount) && !isAttended(userAccount);
    }

    public boolean isDisEnrollableFor(UserAccount userAccount) {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now()) &&
                isAlreadyEnrolled(userAccount) && !isAttended(userAccount);
    }

    public boolean isAlreadyEnrolled(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getAccount().equals(account)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAttended(UserAccount userAccount) {
        Account account = userAccount.getAccount();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getAccount().equals(account) && enrollment.isAttended()) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.eventType == EventType.CONFIRMATIVE &&
                enrollment.getEvent().equals(this) &&
                !enrollment.isAccepted() && !enrollment.isAttended();
    }

    public boolean canReject(Enrollment enrollment ) {
        return this.eventType == EventType.CONFIRMATIVE &&
                enrollment.getEvent().equals(this) &&
                enrollment.isAccepted() && !enrollment.isAttended();
    }

    public int numberOfRemainSpots() {
        return this.limitOfEnrollments - (int)this.enrollments.stream().filter(Enrollment::isAccepted).count();
    }
}
