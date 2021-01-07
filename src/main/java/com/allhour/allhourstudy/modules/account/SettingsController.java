package com.allhour.allhourstudy.modules.account;

import com.allhour.allhourstudy.modules.account.form.NicknameForm;
import com.allhour.allhourstudy.modules.account.form.Notifications;
import com.allhour.allhourstudy.modules.account.form.PasswordForm;
import com.allhour.allhourstudy.modules.account.form.Profile;
import com.allhour.allhourstudy.modules.account.validator.NicknameValidator;
import com.allhour.allhourstudy.modules.account.validator.PasswordFormValidator;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.tag.TagForm;
import com.allhour.allhourstudy.modules.tag.TagRepository;
import com.allhour.allhourstudy.modules.zone.Zone;
import com.allhour.allhourstudy.modules.zone.ZoneForm;
import com.allhour.allhourstudy.modules.zone.ZoneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @GetMapping("/settings/profile")
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("profile", modelMapper.map(account, Profile.class));
        return "settings/profile";
    }

    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @ModelAttribute @Valid Profile profile,
                                Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/profile";
        }
        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다");
        return "redirect:" + "/settings/profile";
    }

    @GetMapping("/settings/password")
    public String passwordUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("passwordForm", new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@CurrentUser Account account, @ModelAttribute @Valid PasswordForm passwordForm,
                                 Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/password";
        }
        accountService.updatePassword(account, passwordForm);
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다");
        return "redirect:" + "/settings/password";
    }

    @GetMapping("/settings/notifications")
    public String updateNotificationForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("notifications", modelMapper.map(account, Notifications.class));
        return "settings/notifications";
    }


    @PostMapping("/settings/notifications")
    public String updateNotification(@CurrentUser Account account, @ModelAttribute @Valid Notifications notifications,
                                     Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/notifications";
        }
        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 업데이트 했습니다");
        return "redirect:" + "/settings/notifications";
    }

    @InitBinder("nicknameForm")
    public void nicknameInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping("/settings/account")
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("nicknameForm", modelMapper.map(account, NicknameForm.class));
        return "settings/account";
    }

    @PostMapping("/settings/account")
    public String updateAccount(@CurrentUser Account account, @ModelAttribute @Valid NicknameForm nicknameForm,
                                Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute("account", account);
            return "settings/account";
        }
        accountService.updateNickname(account, nicknameForm);
        attributes.addFlashAttribute("message", "닉네임을 변경했습니다");
        return "redirect:" + "/settings/account";
    }

    @GetMapping("/settings/tags")
    public String updateTags(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute("account", account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
        return "settings/tags";
    }

    @ResponseBody
    @PostMapping("/settings/tags/add")
    public ResponseEntity addTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String tagTitle = tagForm.getTagTitle();
        Tag byTitle = tagRepository.findByTitle(tagTitle);
        if (byTitle == null) {
            Tag tag = new Tag();
            tag.setTitle(tagTitle);
            byTitle = tagRepository.save(tag);
        }
        accountService.addTag(account, byTitle);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/settings/tags/remove")
    public ResponseEntity removeTag(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings/zones")
    public String updateZones(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute("account", account);
        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "settings/zones";
    }


    @PostMapping("/settings/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentUser Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(),zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }
}
