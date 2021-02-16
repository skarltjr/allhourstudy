package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study,Long>,StudyRepositoryExtension {
    boolean existsByPath(String path);
    /**  load - 엔티티그래프 요소들은 즉시로딩 / 나머지는 lazy
     *   fetch - 엔티티그래프 요소들은 즉시로딩 / 나머지는 디폴트 혹은 지정값 그대로*/

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

    @EntityGraph(value = "Study.withTagsAndZones",type = EntityGraph.EntityGraphType.FETCH)
    List<Study> findFirst9ByPublishedOrderByPublishedDateTimeDesc(boolean b);

    List<Study> findFirst5ByManagersContainingAndPublishedOrderByPublishedDateTimeDesc(Account current, boolean b);

    List<Study> findFirst5ByMembersContainingAndPublishedOrderByPublishedDateTimeDesc(Account current, boolean b);

}
