package com.allhour.allhourstudy.modules.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @BeforeEach
    void beforeEach() {
        accountRepository.deleteAll();
    }


    @Test
    @DisplayName("패스워드 수정 폼")
    @WithAccount("kiseok")
    void updatePasswordForm()throws Exception {
        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }
    @Test
    @DisplayName("패스워드 수정 -정상 처리")
    @WithAccount("kiseok")
    void updatePassword()throws Exception {
        String password = "999999999";
        mockMvc.perform(post("/settings/password")
                .param("newPassword",password)
                .param("newPasswordConfirm","999999999")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        Account kiseok = accountRepository.findByNickname("kiseok");
        assertThat(passwordEncoder.matches("999999999", kiseok.getPassword()));
    }

    @Test
    @DisplayName("패스워드 수정 -오류 - 불일치")
    @WithAccount("kiseok")
    void updatePassword_withWrong()throws Exception {
        mockMvc.perform(post("/settings/password")
                .param("newPassword","999999999")
                .param("newPasswordConfirm","111111111")
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

}