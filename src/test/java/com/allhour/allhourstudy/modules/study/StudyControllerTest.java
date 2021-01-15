package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.account.AccountRepository;
import com.allhour.allhourstudy.modules.account.AccountService;
import com.allhour.allhourstudy.modules.account.WithAccount;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class StudyControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    StudyService studyService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;

    @AfterEach
    public void afterEach() {
        studyRepository.deleteAll();
    }

    @Test
    @WithAccount("kiseok")
    @DisplayName("스터디 개설 폼")
    void newStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
    }

    @Test
    @WithAccount("kiseok")
    @DisplayName("스터디 개설")
    void createStudy() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "test-path")
                .param("title", "testStudy")
                .param("shortDescription", "짧은 소개입니다")
                .param("fullDescription", "긴 소개입니다")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        Study study = studyRepository.findByPath("test-path");
        assertNotNull(study);
    }

    @Test
    @WithAccount("kiseok")
    @DisplayName("스터디 개설 - 실패")
    void createStudy_fail() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "wrong path")
                .param("title", "testStudy")
                .param("shortDescription", "짧은 소개입니다")
                .param("fullDescription", "긴 소개입니다")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"));
        Study study = studyRepository.findByPath("test-path");
        assertNull(study);
    }

    @Test
    @WithAccount("kiseok")
    @DisplayName("스터디 조회하기")
    void viewStudy() throws Exception {
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("test-path");
        studyForm.setTitle("test");
        studyForm.setShortDescription("짧은 소개");
        studyForm.setFullDescription("긴 소개");
        Account kiseok = accountRepository.findByNickname("kiseok");
        studyService.createNewStudy(studyForm, kiseok);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("kiseok")
    @DisplayName("스터디 가입하기")
    void joinStudy() throws Exception {
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("test-path");
        studyForm.setTitle("test");
        studyForm.setShortDescription("짧은 소개");
        studyForm.setFullDescription("긴 소개");

        Account account = new Account();
        account.setNickname("kiseok2");
        account.setEmail("test@email.com");
        Account newAccount = accountRepository.save(account);

        Account kiseok = accountRepository.findByNickname("kiseok");
        Study newStudy = studyService.createNewStudy(studyForm, newAccount);

        mockMvc.perform(get("/study/test-path/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8) + "/members"));

        assertThat(newStudy.getManagers().contains(kiseok));

    }

    @Test
    @WithAccount("kiseok")
    @DisplayName("스터디 탈퇴하기")
    void disJoinStudy()throws Exception {
        StudyForm studyForm = new StudyForm();
        studyForm.setPath("test-path");
        studyForm.setTitle("test");
        studyForm.setShortDescription("짧은 소개");
        studyForm.setFullDescription("긴 소개");

        Account account = new Account();
        account.setNickname("kiseok2");
        account.setEmail("test@email.com");
        Account newAccount = accountRepository.save(account);

        Study newStudy = studyService.createNewStudy(studyForm, newAccount);
        Account kiseok = accountRepository.findByNickname("kiseok");
        newStudy.getMembers().add(kiseok);

        mockMvc.perform(get("/study/test-path/disJoin"))
                .andExpect(redirectedUrl("/study/" + URLEncoder.encode(newStudy.getPath(), StandardCharsets.UTF_8) + "/members"));
        assertFalse(newStudy.getMembers().contains(kiseok));

    }
}