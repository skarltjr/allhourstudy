package com.allhour.allhourstudy.modules.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepositoryExtension {
    Page<Board> findLists(Pageable pageable);
}
