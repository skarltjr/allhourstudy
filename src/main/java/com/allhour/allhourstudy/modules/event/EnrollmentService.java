package com.allhour.allhourstudy.modules.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentService {

    public void checkIn(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void noneCheckIn(Enrollment enrollment) {
        enrollment.setAttended(false);
    }
}
