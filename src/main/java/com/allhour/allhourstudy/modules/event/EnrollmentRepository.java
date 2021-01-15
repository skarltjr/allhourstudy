package com.allhour.allhourstudy.modules.event;

import com.allhour.allhourstudy.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @EntityGraph(value = "Enrollment.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Enrollment findByEventAndAccount(Event event, Account account);

    @EntityGraph(value = "Enrollment.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Enrollment findWithAllById(Long enrollId);

    @EntityGraph(value = "Enrollment.withAllAndStudy", type = EntityGraph.EntityGraphType.FETCH)
    List<Enrollment> findByAccountAndAcceptedOrderByEnrolledAtDesc(Account current, boolean b);
}
