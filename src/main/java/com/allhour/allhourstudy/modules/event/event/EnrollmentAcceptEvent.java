package com.allhour.allhourstudy.modules.event.event;

import com.allhour.allhourstudy.modules.event.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentAcceptEvent {
    private final Enrollment enrollment;

}
