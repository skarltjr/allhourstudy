package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.CurrentUser;
import com.allhour.allhourstudy.modules.study.form.StudyDescriptionForm;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.tag.TagForm;
import com.allhour.allhourstudy.modules.tag.TagRepository;
import com.allhour.allhourstudy.modules.tag.TagService;
import com.allhour.allhourstudy.modules.zone.Zone;
import com.allhour.allhourstudy.modules.zone.ZoneForm;
import com.allhour.allhourstudy.modules.zone.ZoneRepository;
import com.allhour.allhourstudy.modules.zone.ZoneService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StudySettingsController {

    private final ObjectMapper objectMapper;
    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;
    private final ZoneService zoneService;

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

    @GetMapping("/study/{path}/settings/banner")
    public String studyBanner(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        return "study/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/enable")
    public String useBanner(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.useBanner(study);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8)+ "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/disable")
    public String unUseBanner(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.unUsedBanner(study);
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner")
    public String settingBanner(@CurrentUser Account account, @PathVariable String path, String image,
                                RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateBanner(study, image);
        attributes.addFlashAttribute("message", "스터디 배너를 수정했습니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8)+ "/settings/banner";
    }

    /**     <div th:replace="fragments.html :: study-info"></div> 에서 멤버 , zone 등을 부르기 때문에 추가쿼리 당연히
     * 발생*/

    @GetMapping("/study/{path}/settings/tags")
    public String studyTags(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyWithTags(account, path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return "study/settings/tags";
    }

    @PostMapping("/study/{path}/settings/tags/add")
    @ResponseBody
    public ResponseEntity addStudyTags(@CurrentUser Account account, @PathVariable String path,
                                       @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyWithTags(account, path);
        Tag tag = tagService.findOrCreate(tagForm);
        studyService.addTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/study/{path}/settings/tags/remove")
    @ResponseBody
    public ResponseEntity removeStudyTags(@CurrentUser Account account, @PathVariable String path,
                                          @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyWithTags(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTag(study, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study/{path}/settings/zones")
    public String studyZones(@CurrentUser Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyWithZones(account, path);
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("zones", study.getZones().stream().map(Zone::toString).collect(Collectors.toList()));
        log.info("hello");
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        log.info("next");
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        log.info("last");
        return "study/settings/zones";
    }

    @PostMapping("/study/{path}/settings/zones/add")
    @ResponseBody
    public ResponseEntity addStudyZones(@CurrentUser Account account, @PathVariable String path,
                                        @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyWithZones(account, path);
        Zone zone = zoneService.findOrCreate(zoneForm);
        studyService.addZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/study/{path}/settings/zones/remove")
    @ResponseBody
    public ResponseEntity removeStudyZones(@CurrentUser Account account, @PathVariable String path,
                                           @RequestBody ZoneForm zoneForm) {
        Study study = studyService.getStudyWithZones(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeZone(study, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/study/{path}/settings/study")
    public String studySetting(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        /** 여기서 만약 getStudyToUpdate -> getStudyToUpdateStatus하면 여기로 리다이렉트할 때
         * <div th:replace="fragments.html :: study-info"></div> 가 멤버 태그 존을 다 필요로해서
         * 추가쿼리가 발생 . getStudyToUpdate미리 다 땡겨오면 추가쿼리 x */
        model.addAttribute("account", account);
        model.addAttribute("study", study);
        return "study/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/publish")
    public String publishStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        studyService.publish(study);
        attributes.addFlashAttribute("message", "스터디를 공개했습니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/close")
    public String closeStudy(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        studyService.close(study);
        attributes.addFlashAttribute("message", "스터디를 종료했습니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/recruit/start")
    public String startRecruit(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        if (!study.canUpdateRecruit()) {
            attributes.addFlashAttribute("message", "마지막 인원모집 설정 1시간 이 후부터 재설정이 가능합니다.");
            return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
        }
        studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "모집을 시작합니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/recruit/stop")
    public String stopRecruit(@CurrentUser Account account, @PathVariable String path, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        if (!study.canUpdateRecruit()) {
            attributes.addFlashAttribute("message", "마지막 인원모집 설정 1시간 이 후부터 재설정이 가능합니다.");
            return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
        }
        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "모집을 종료합니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/path")
    public String changePath(@CurrentUser Account account, @PathVariable String path,
                             @RequestParam String newPath, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        if (!studyService.isValidPath(newPath)) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("studyPathError", "해당 경로는 사용할 수 없습니다.");
            return "study/settings/study";
        }
        studyService.updatePath(study, newPath);
        attributes.addFlashAttribute("message", "스터디 경로를 변경했습니다.");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/title")
    public String changeTitle(@CurrentUser Account account, @PathVariable String path,
                              @RequestParam String newTitle, Model model, RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdateStatus(path, account);
        if (!studyService.isValidTitle(newTitle)) {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("studyTitleError", "사용 가능한 이름이 아닙니다.");
            return "study/settings/study";
        }
        studyService.updateTitle(study, newTitle);
        attributes.addFlashAttribute("message", "스터디이름을 수정했습니다.");
        return "redirect:/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/study";
    }

    @PostMapping("/study/{path}/settings/study/remove")
    public String removeStudy(@CurrentUser Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdateStatus(path,account);
        studyService.remove(study);
        return "redirect:/";
    }
}