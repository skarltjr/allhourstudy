package com.allhour.allhourstudy.modules.board;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryExtension {

    @EntityGraph(value = "Board.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Board findWithAllById(Long boardId);

}
