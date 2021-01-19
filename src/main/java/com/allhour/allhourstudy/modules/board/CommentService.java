package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.board.form.CommentForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void addComment(Account account, Board board, CommentForm commentForm) {
        Comment newComment = new Comment();
        newComment.setCommentWriter(account);
        newComment.setBoard(board);
        newComment.setChat(commentForm.getChat());
        newComment.setCreatedDateTime(LocalDateTime.now());
        newComment.setUpdatedDateTime(LocalDateTime.now());
        Comment saved = commentRepository.save(newComment);
        board.getComments().add(saved);
    }


    public void deleteComment(Comment comment, Board board) {
        board.getComments().remove(comment);
        comment.setBoard(null);
        commentRepository.delete(comment);
    }
}
