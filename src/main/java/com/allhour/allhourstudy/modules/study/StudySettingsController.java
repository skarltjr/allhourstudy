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
        return "redirect:/study/" + URLEncoder.encode(study.getPath()) + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner/disable")
    public String unUseBanner(@CurrentUser Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.unUsedBanner(study);
        return "redirect:/study/" + URLEncoder.encode(study.getPath()) + "/settings/banner";
    }

    @PostMapping("/study/{path}/settings/banner")
    public String settingBanner(@CurrentUser Account account, @PathVariable String path, String image,
                                RedirectAttributes attributes) {
        Study study = studyService.getStudyToUpdate(path, account);
        studyService.updateBanner(study, image);
        attributes.addFlashAttribute("message", "스터디 배너를 수정했습니다");
        return "redirect:/study/" + URLEncoder.encode(study.getPath()) + "/settings/banner";
    }

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
}
