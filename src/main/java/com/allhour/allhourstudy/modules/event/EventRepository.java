package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStudyOrderByStartDateTime(Study study);
}
