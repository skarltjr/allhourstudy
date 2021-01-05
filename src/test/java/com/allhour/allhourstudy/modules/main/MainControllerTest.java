package com.allhour.allhourstudy.modules.main;

import com.allhour.allhourstudy.modules.account.AccountService;
import com.allhour.allhourstudy.modules.account.form.SignUpForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;

    @Test
    void login() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("kiseok@naver.com");
        signUpForm.setNickname("kiseok");
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm);

        mockMvc.perform(post("/login")
                .param("username", "kiseok@naver.com")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("kiseok"));
    }

    @Test
    void login_withNickname() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("kiseok@naver.com");
        signUpForm.setNickname("kiseok");
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm);

        mockMvc.perform(post("/login")
                .param("username", "kiseok")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("kiseok"));
    }

    @Test
    @DisplayName("로그인 실패 - 정보 입력 오류")
    void login_Wrong() throws Exception {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("kiseok@naver.com");
        signUpForm.setNickname("kiseok");
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm);

        mockMvc.perform(post("/login")
                .param("username", "qweqwe@naver.com")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

   @Test
    void logout() throws Exception {
       mockMvc.perform(post("/logout")
               .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/"))
               .andExpect(unauthenticated());
   }
}