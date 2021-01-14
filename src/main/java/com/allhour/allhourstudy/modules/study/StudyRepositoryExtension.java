package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

    Page<Study> findByKeyword(String keyWord, Pageable pageable);

    List<Study> findByAccountWithTagsAndZones(Account current);
}
