package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.QAccount;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import static com.allhour.allhourstudy.modules.account.QAccount.account;
import static com.allhour.allhourstudy.modules.board.QBoard.board;
import static com.allhour.allhourstudy.modules.board.QComment.comment;

public class BoardRepositoryExtensionImpl extends QuerydslRepositorySupport implements BoardRepositoryExtension {
    public BoardRepositoryExtensionImpl() {
        super(Board.class);
    }

    @Override
    public Page<Board> findLists(Pageable pageable) {
        JPQLQuery<Board> query = from(board)
                .leftJoin(board.writer, account).fetchJoin()
                .leftJoin(board.comments, comment).fetchJoin()
                .distinct();
        JPQLQuery<Board> paging = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Board> result = paging.fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }
}
