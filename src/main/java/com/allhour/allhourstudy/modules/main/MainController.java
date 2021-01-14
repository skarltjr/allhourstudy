package com.allhour.allhourstudy.modules.main;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.AccountRepository;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.event.Enrollment;
import com.allhour.allhourstudy.modules.event.EnrollmentRepository;
import com.allhour.allhourstudy.modules.study.Study;
import com.allhour.allhourstudy.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (account != null) {
            Account current = accountRepository.findWithTagsAndZonesById(account.getId());
            model.addAttribute("account", current);
            //enrollmentList
            List<Enrollment> enrollments = enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(current, true);
            model.addAttribute("enrollmentList", enrollments);
            //studyList
            List<Study> studies = studyRepository.findByAccountWithTagsAndZones(current);
            model.addAttribute("studyList", studies);
            //studyManagerOf
            List<Study> managers = studyRepository.findFirst5ByManagersContainingAndPublishedOrderByPublishedDateTimeDesc(current, true);
            model.addAttribute("studyManagerOf", managers);
            //studyMemberOf
            List<Study> members = studyRepository.findFirst5ByMembersContainingAndPublishedOrderByPublishedDateTimeDesc(current, true);
            model.addAttribute("studyMemberOf", members);
            return "index-after-login";
        }
        List<Study> list = studyRepository.findFirst9ByPublishedOrderByPublishedDateTimeDesc(true);
        model.addAttribute("studyList", list);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/study")
    public String search(@RequestParam("keyword") String keyWord, Model model, @PageableDefault(
            size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Study> studies = studyRepository.findByKeyword(keyWord, pageable);
        model.addAttribute("studyPage", studies);
        model.addAttribute("keyword", keyWord);
        model.addAttribute("sortProperty",
                pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }

}
