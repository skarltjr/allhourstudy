package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.UserAccount;
import com.allhour.allhourstudy.modules.study.Study;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Event {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // 이벤트 - 스터디는 거의 항상 같이 다닌다.
    private Study study;

    @ManyToOne(fetch = FetchType.EAGER)
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
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
    private List<Enrollment> enrollments = new ArrayList<>();

    public boolean isEnrollableFor(UserAccount userAccount) {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now()) &&
                !isAlreadyEnrolled(userAccount) && !isAttended(userAccount);
    }

    public boolean isDisEnrollableFor(UserAccount userAccount) {
        return !this.endEnrollmentDateTime.isAfter(LocalDateTime.now()) &&
                !isAlreadyEnrolled(userAccount) && isAttended(userAccount);
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
                enrollment.getEvent().equals(this) &&   //todo check
                !enrollment.isAccepted() && !enrollment.isAttended();
    }

    public boolean canReject(Enrollment enrollment ) {
        return this.eventType == EventType.CONFIRMATIVE &&
                enrollment.getEvent().equals(this) &&   //todo check
                enrollment.isAccepted() && !enrollment.isAttended();
    }
}
