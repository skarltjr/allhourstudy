package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.board.form.BoardForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final CommentService commentService;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;


    //todo  부트스트랩으로 인증정보가 있으면 글작성등 인증정보 or 본인체크

    /**
     * 전체 글 조회
     */

    @GetMapping("/boards")
    public String viewAllBoard(@CurrentUser Account account, Model model, @PageableDefault(
            size = 10, sort = "createdDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("account", account);
        Page<Board> boardPage = boardRepository.findLists(pageable);
        //가져올 때 댓글 개수까지 달 수 있도록 양방향으로 댓글 - 게시글
        model.addAttribute("boardLists", boardPage);
        return "boards";
    }

    /**
     * 글 작성
     */

    @GetMapping("/board/add")
    public String writeBoardForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("boardForm", new BoardForm());
        return "boadrs/form";
    }

    @PostMapping("/boards/add")
    public String writeBoard(@CurrentUser Account account, @ModelAttribute @Valid BoardForm boardForm, Errors errors,
                             Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "boadrs/form";
        }
        Board board = boardService.createNewBoard(account, boardForm);
        return "redirect:/boards/" + board.getId();
    }

    /**
     * 글 단건 조회
     */
    @GetMapping("/boards/{boardId}")
    public String viewBoard(@CurrentUser Account account, @PathVariable Long boardId, Model model) {
        // 작성자한테는 수정 삭제가 버튼이 가능하고 나머지한테는 안보이도록 프론트단에서
        Board board = boardRepository.findWithAllById(boardId);
        boardService.addViewCount(board);
        model.addAttribute("account", account);
        model.addAttribute("board", board);
        List<Comment> comments = commentRepository.findByBoard(board);
        model.addAttribute("comments", comments);
        return "redirect:/boards/" + board.getId();
    }

    @GetMapping("/boards/{boardId}/edit")
    public String editBoardForm(@CurrentUser Account account, @PathVariable Long boardId, Model model) {
        //todo 본인인지 체크 - 프론트 or 백단에서 AccessDeniedException 던지기
        Board board = boardRepository.findWithAllById(boardId);
        model.addAttribute("account", account);
        model.addAttribute("boardForm", modelMapper.map(board, BoardForm.class));
        return "boards/edit";
    }

    @PostMapping("/boards/{boardId}/edit")
    public String editBoard(@CurrentUser Account account, @PathVariable Long boardId, @ModelAttribute @Valid
            BoardForm boardForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "boards/edit";
        }
        Board board = boardRepository.findWithAllById(boardId);
        boardService.updateBoard(board, boardForm);
        return "redirect:/boards/" + board.getId();
    }

    @PostMapping("/boards/{boardId}/delete")
    public String deleteBoard(@CurrentUser Account account, @PathVariable Long boardId) {
        //todo 본인인지 체크 - 프론트 or 백단에서
        Board board = boardRepository.findWithAllById(boardId);
        boardService.deleteBoard(board);
        return "redirect:/boards";
    }

    //todo 댓글 add -  edit - delete


    /**         댓글도  form으로 받아야하나 ..... */

    @PostMapping("/boards/{boardId}/comments/add")
    public String comment(@CurrentUser Account account, @PathVariable Long boardId, @RequestParam("comment") String comment) {
        Board board = boardRepository.findWithAllById(boardId);
        commentService.addComment(account, board, comment);
        return "redirect:/boards/" + board.getId();
    }

    /*@PostMapping("/boards/{boardId}/comments/edit")
    public String editComments(@CurrentUser Account account, @PathVariable Long boardId, @RequestParam("comment") String comment) {

    }*/
}

