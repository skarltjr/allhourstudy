package com.allhour.allhourstudy.modules.study.event;

import com.allhour.allhourstudy.modules.study.Study;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StudyCreatedEvent {

    private final Study study;

}
