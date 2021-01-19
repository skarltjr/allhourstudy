package com.allhour.allhourstudy.modules.board;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "Comment.withAll", type = EntityGraph.EntityGraphType.FETCH)
    void deleteAllByBoard(Board board);

    @EntityGraph(value = "Comment.withAll", type = EntityGraph.EntityGraphType.FETCH)
    List<Comment> findByBoard(Board board);

    @EntityGraph(value = "Comment.withAll", type = EntityGraph.EntityGraphType.FETCH)
    Comment findWithAllById(Long commentId);
}
