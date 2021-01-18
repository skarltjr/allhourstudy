package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.board.form.BoardForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public Board createNewBoard(Account account, BoardForm boardForm) {
        Board board = modelMapper.map(boardForm, Board.class);
        board.setWriter(account);
        board.setViewCount(0);
        board.setCreatedDateTime(LocalDateTime.now());
        board.setUpdatedDateTime(LocalDateTime.now());
        return boardRepository.save(board);
    }

    public void updateBoard(Board board, BoardForm boardForm) {
        modelMapper.map(boardForm, board);
        //dirty checking  - persistence
    }

    public void deleteBoard(Board board) {
        commentRepository.deleteAllByBoard(board);
        boardRepository.delete(board);
    }

    public void addViewCount(Board board) {
        board.addView();
        //todo 어떤 경우에 조회수를 증가시킬건가 ? 하루단위 ? 계정단위?
    }
}
