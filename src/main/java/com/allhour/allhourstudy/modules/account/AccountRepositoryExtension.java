package com.allhour.allhourstudy.modules.account;

import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.zone.Zone;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface AccountRepositoryExtension {

    List<Account> findAccounts(Set<Tag> tags, Set<Zone> zones);
}
