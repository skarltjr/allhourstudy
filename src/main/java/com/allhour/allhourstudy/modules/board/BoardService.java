package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.board.form.BoardForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    public Board createNewBoard(Account account, BoardForm boardForm) {
        Board board = modelMapper.map(boardForm, Board.class);
        board.setWriter(account);
        board.setViewCount(0);
        board.setCreatedDateTime(LocalDateTime.now());
        board.setUpdatedDateTime(LocalDateTime.now());
        return boardRepository.save(board);
    }
}
