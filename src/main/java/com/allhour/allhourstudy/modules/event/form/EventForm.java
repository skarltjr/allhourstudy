package com.allhour.allhourstudy.modules.event.form;

import com.allhour.allhourstudy.modules.event.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    private EventType eventType = EventType.FCFS;

    private String description;

    @Min(2)
    private Integer limitOfEnrollments = 2;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  //기본포맷설정
    private LocalDateTime startDateTime; //모임 시작

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDateTime; // 모임 종료

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endEnrollmentDateTime;
}
