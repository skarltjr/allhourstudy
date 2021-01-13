package com.allhour.allhourstudy.modules.account;

import com.allhour.allhourstudy.modules.tag.QTag;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.zone.QZone;
import com.allhour.allhourstudy.modules.zone.Zone;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

import static com.allhour.allhourstudy.modules.account.QAccount.account;
import static com.allhour.allhourstudy.modules.tag.QTag.tag;
import static com.allhour.allhourstudy.modules.zone.QZone.zone;

public class AccountRepositoryExtensionImpl extends QuerydslRepositorySupport implements AccountRepositoryExtension {
    public AccountRepositoryExtensionImpl() {
        super(Account.class);
    }

    @Override
    public List<Account> findAccounts(Set<Tag> tags, Set<Zone> zones) {
        JPQLQuery<Account> query = from(account)
                .where(account.tags.any().in(tags)
                        .or(account.zones.any().in(zones)))
                .leftJoin(account.tags, tag).fetchJoin()
                .leftJoin(account.zones, zone).fetchJoin()
                .distinct();
        return query.fetch();
    }
}
