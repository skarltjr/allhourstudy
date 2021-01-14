package com.allhour.allhourstudy.modules.account;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account,Long>,AccountRepositoryExtension {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String emailOrNickname);

    @EntityGraph(value = "Account.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    Account findWithTagsAndZonesById(Long id);
}
