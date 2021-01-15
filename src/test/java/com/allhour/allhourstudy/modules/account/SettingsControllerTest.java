package com.allhour.allhourstudy.modules.account;

import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.tag.TagForm;
import com.allhour.allhourstudy.modules.tag.TagRepository;
import com.allhour.allhourstudy.modules.zone.Zone;
import com.allhour.allhourstudy.modules.zone.ZoneForm;
import com.allhour.allhourstudy.modules.zone.ZoneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    ObjectMapper objectMapper;


    @AfterEach
    void AfterEach() {
        accountRepository.deleteAll();
        tagRepository.deleteAll();
        zoneRepository.deleteAll();
    }


    @Test
    @DisplayName("패스워드 수정 폼")
    @WithAccount("kiseok")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @Test
    @DisplayName("패스워드 수정 -정상 처리")
    @WithAccount("kiseok")
    void updatePassword() throws Exception {
        String password = "999999999";
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "999999999")
                .param("newPasswordConfirm", "999999999")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        Account kiseok = accountRepository.findByNickname("kiseok");
        assertThat(passwordEncoder.matches("999999999", kiseok.getPassword()));
    }

    @Test
    @DisplayName("패스워드 수정 -오류 - 불일치")
    @WithAccount("kiseok")
    void updatePassword_withWrong() throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword", "999999999")
                .param("newPasswordConfirm", "111111111")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));

    }

    @Test
    @DisplayName("프로필 수정 - 정상")
    @WithAccount("kiseok")
    void updateProfile() throws Exception {

        String location = "지역정보";
        mockMvc.perform(post("/settings/profile")
                .param("bio", "소개 수정")
                .param("location", location)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account kiseok = accountRepository.findByNickname("kiseok");
        assertThat(location.equals(kiseok.getLocation()));
    }


    @Test
    @DisplayName("프로필 수정 - 오류")
    @WithAccount("kiseok")
    void updateProfile_withWrongInput() throws Exception {
        String location = "너무길게하면 안됩니다~!~!~!~!~!~1 너무길게하면 안됩니다~!~!~!~!~!~1 너무길게하면 안됩니다~!~!~!~!~!~1" +
                "너무길게하면 안됩니다~!~!~!~!~!~1 너무길게하면 안됩니다~!~!~!~!~!~1 너무길게하면 안됩니다~!~!~!~!~!~1";
        mockMvc.perform(post("/settings/profile")
                .param("bio", "소개 수정")
                .param("location", location)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

    }

    @WithAccount("kiseok")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get("/settings/tags"))
                .andExpect(view().name("settings/tags"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));

    }

    @Test
    @DisplayName("계정에 태그 추가")
    @WithAccount("kiseok")
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("java");

        mockMvc.perform(post("/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());
        Tag java = tagRepository.findByTitle("java");
        Account kiseok = accountRepository.findByNickname("kiseok");
        assertThat(kiseok.getTags().contains(java));
    }

    @Test
    @DisplayName("계정 태그 삭제")
    @WithAccount("kiseok")
    void removeTag() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Tag tag = new Tag();
        tag.setTitle("spring");
        Tag save = tagRepository.save(tag);
        accountService.addTag(kiseok, save);

        assertThat(kiseok.getTags().contains(save));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("spring");

        mockMvc.perform(post("/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());
        assertFalse(kiseok.getTags().contains(save));
    }

    @WithAccount("kiseok")
    @DisplayName("계정의 지역 정보 수정 폼")
    @Test
    void updateZonesForm() throws Exception {
        mockMvc.perform(get("/settings/zones"))
                .andExpect(view().name("settings/zones"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    private Zone testZone = Zone.builder().city("test").localNameOfCity("테스트시").province("테스트주").build();

    @Test
    @DisplayName("계정 지역정보 추가 - 정상")
    @WithAccount("kiseok")
    void addZones() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());
        zoneRepository.save(testZone);

        mockMvc.perform(post("/settings/zones/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());
        Account kiseok = accountRepository.findByNickname("kiseok");
        Zone zone2 = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertThat(kiseok.getZones().contains(zone2));

    }

    @Test
    @DisplayName("계정 지역정보 삭제")
    @WithAccount("kiseok")
    void removeZones() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(testZone.toString());
        zoneRepository.save(testZone);

        mockMvc.perform(post("/settings/zones/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(zoneForm))
                .with(csrf()))
                .andExpect(status().isOk());
        Account kiseok = accountRepository.findByNickname("kiseok");
        Zone zone2 = zoneRepository.findByCityAndProvince(testZone.getCity(), testZone.getProvince());
        assertFalse(kiseok.getZones().contains(zone2));
    }
}