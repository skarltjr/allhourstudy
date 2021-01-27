package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import com.allhour.allhourstudy.modules.study.validator.StudyFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyFormValidator studyFormValidator;
    private final StudyService studyService;
    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyFormValidator);
    }

    @GetMapping("/new-study")
    public String newStudyForm(@CurrentUser Account account, Model model) {

        model.addAttribute("account", account);
        model.addAttribute("studyForm", new StudyForm());
        return "study/form";
    }

    @PostMapping("/new-study")
    public String createStudy(@CurrentUser Account account, @ModelAttribute @Valid StudyForm studyForm, Errors errors,
                              Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "study/form";
        }
        Study newStudy = studyService.createNewStudy(studyForm, account);
        return "redirect:/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8);
    }


    @GetMapping("/study/{path}")
    public String studyView(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        return "study/view";
    }

    @GetMapping("/study/{path}/members")
    public String studyMembersView(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        return "study/members";
    }

    @GetMapping("/study/{path}/join")  //todo Http get에 로직을 설정하지말자 -> post로 변경하기
    public String joinStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyRepository.findWithAllMemberByPath(path);
        studyService.memberJoin(study, account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/members";
    }

    @GetMapping("/study/{path}/disJoin")
    public String disJoinStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyRepository.findWithAllMemberByPath(path);
        studyService.memberDisjoin(study, account);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/members";
    }

    //테스트용 데이터생성
   /* @GetMapping("/study/generateTest")
    public String generateTests(@CurrentUser Account account) {
        studyService.generate(account);
        return "redirect:/";
    }*/
}
