package com.allhour.allhourstudy.modules.study;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study,Long>,StudyRepositoryExtension {
    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll",type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(value = "Study.withTagsAndManagers",type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(value = "Study.withZonesAndManagers",type = EntityGraph.EntityGraphType.FETCH)
    Study findWithZonesByPath(String path);

    @EntityGraph(value = "Study.withManagers",type = EntityGraph.EntityGraphType.FETCH)
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(value = "Study.withManagersAndMembers",type = EntityGraph.EntityGraphType.FETCH)
    Study findWithAllMemberByPath(String path);

    @EntityGraph(value = "Study.withTagsAndZones",type = EntityGraph.EntityGraphType.FETCH)
    Study findWithTagsAndZonesById(Long id);

}
