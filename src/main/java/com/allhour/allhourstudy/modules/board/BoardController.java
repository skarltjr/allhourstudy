package com.allhour.allhourstudy.modules.board;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.board.form.BoardForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;

    /**     전체 글 조회 */

    @GetMapping("/boards")
    public String viewAllBoard(@CurrentUser Account account, Model model, @PageableDefault(
            size = 10,sort = "createdDateTime",direction = Sort.Direction.DESC)Pageable pageable) {
        // 프론트단에서 인증정보가 있으면 글작성하기버튼 on 없으면 off
        model.addAttribute("account", account);
        //글 10개씩  todo 여기부터 다시확인
        Page<Board> boardPage = boardRepository.findLists(pageable);
        model.addAttribute("boardLists", boardPage);
        return "boards";
    }

    /**     글 작성 */

    @GetMapping("/board/add")
    public String writeBoardForm(@CurrentUser Account account, Model model) {
        // 로그인한 사람만가능
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
    public String viewBoard(@CurrentUser Account account,@PathVariable Long boardId, Model model) {
        // 작성자한테는 수정 삭제가 버튼이 가능하고 나머지한테는 안보이도록 프론트단에서
        Board board = boardRepository.findById(boardId).orElseThrow();
        model.addAttribute("account", account);
        model.addAttribute("board", board);
        return "redirect:/boards/" + board.getId();
    }

    //삭제
}

