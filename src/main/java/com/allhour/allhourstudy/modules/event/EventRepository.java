package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.study.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(value = "Event.withEnrollments",type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByStudyOrderByStartDateTime(Study study);

    @EntityGraph(value = "Event.withAll",type = EntityGraph.EntityGraphType.LOAD)
    Event findWithEnrollmentsById(Long id);
}
