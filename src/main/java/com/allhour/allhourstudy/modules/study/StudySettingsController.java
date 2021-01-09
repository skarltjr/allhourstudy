package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.study.form.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
public class StudySettingsController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;

    @GetMapping("/study/{path}/settings/description")
    public String studySettingView(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("studyDescriptionForm", modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/study/{path}/settings/description")
    public String updateStudyDescription(@CurrentUser Account account, @PathVariable String path,
                                         @ModelAttribute @Valid StudyDescriptionForm form, Errors errors, Model model,
                                         RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            return "study/settings/description";
        }
        studyService.updateDescription(form, study);
        attributes.addFlashAttribute("message", "소개를 수정했습니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath()) + "/settings/description";
    }
}
