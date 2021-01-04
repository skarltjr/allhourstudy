package com.allhour.allhourstudy.modules.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @MockBean
    JavaMailSender javaMailSender;

    /* @BeforeEach
     public void beforeEach() {
         accountRepository.deleteAll();
     }*/
    @Test
    @DisplayName("회원가입화면 테스트")
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(view().name("account/sign-up"));
    }

    @Test
    @DisplayName("회원 가입- 정상")
    void signUpSubmit() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "kiseok")
                .param("password", "123456789")
                .param("email", "kiseok@naver.com")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        assertThat(accountRepository.existsByEmail("kiseok@naver.com"));
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("회원 가입- 입력값 오류")
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                .param("nickname", "kiseok")
                .param("password", "123")
                .param("email", "kiseok@naver.com")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"));

    }

    @Test
    @DisplayName("인증메일확인 - 입력값 오류")
    void checkEmailToken_with_wrongInput() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "wag43wsaf")
                .param("email", "test@email.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("error"))
                .andExpect(unauthenticated());

    }
    @Test
    @DisplayName("인증 메일 확인 -정상처리")
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                .email("test@email.com")
                .nickname("kiseok")
                .password("123456789")
                .build();
        Account save = accountRepository.save(account);
        save.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", save.getEmailCheckToken())
                .param("email", save.getEmail()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/checked-email"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(authenticated());
        //@Transactional 추가
        //todo 템플릿엔진 처리 후 다시확인
    }
}