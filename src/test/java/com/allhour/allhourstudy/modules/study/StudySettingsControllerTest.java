package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.*;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.tag.TagForm;
import com.allhour.allhourstudy.modules.tag.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class StudySettingsControllerTest {
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
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    StudyFactory studyFactory;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;


    @AfterEach
    public void afterEach() {
        if (studyRepository.count() > 0) {
            studyRepository.deleteAll();
        }
        //계정먼저지우면 스터디를 못지움
        if (accountRepository.count() > 0) {
            accountRepository.deleteAll();
        }

    }


    @Test
    @DisplayName("스터디소개 수정하기 폼")
    @WithAccount("kiseok")
    void studySettingView() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        mockMvc.perform(get("/study/test-path/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

    @Test
    @DisplayName("스터디소개 수정하기 폼- fail - 권한없음")
    @WithAccount("kiseok")
    void studySettingView_fail() throws Exception {
        Account kiseok2 = accountFactory.createAccount("kiseok2");
        Study study = studyFactory.createStudy("test-path", kiseok2);
        mockMvc.perform(get("/study/test-path/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

    }


    @Test
    @DisplayName("스터디소개 수정하기")
    @WithAccount("kiseok")
    void updateDescription() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);

        mockMvc.perform(post("/study/test-path/settings/description")
                .param("shortDescription", "짧은 소개를 변경합니다")
                .param("fullDescription", "긴소개변경")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + URLEncoder.encode(study.getPath(), StandardCharsets.UTF_8) + "/settings/description"));
    }

    @Test
    @DisplayName("스터디소개 수정하기 - fail")
    @WithAccount("kiseok")
    void updateDescription_fail() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);

        mockMvc.perform(post("/study/test-path/settings/description")
                .param("shortDescription", "")
                .param("fullDescription", "")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));

    }

    @Test
    @DisplayName("스터디 태그 추가하기")
    @WithAccount("kiseok")
    void addStudyTags() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("java");

        mockMvc.perform(post("/study/test-path/settings/tags/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag java = tagRepository.findByTitle("java");
        assertThat(study.getTags().contains(java));
    }

    @Test
    @DisplayName("스터디 태그 삭제하기")
    @WithAccount("kiseok")
    void removeStudyTags() throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        Tag tag = new Tag();
        tag.setTitle("java");
        tagRepository.save(tag);
        study.getTags().add(tag);

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("java");

        mockMvc.perform(post("/study/test-path/settings/tags/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag java = tagRepository.findByTitle("java");
        assertFalse(study.getTags().contains(java));
    }


    @Test
    @DisplayName("스터디 공개")
    @WithAccount("kiseok")
    void publishStudy()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        study.setPublished(false);

        assertThat(study.isPublished()).isFalse();

        mockMvc.perform(post("/study/test-path/settings/study/publish")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(study.isPublished()).isTrue();
    }

    @Test
    @DisplayName("스터디 종료")
    @WithAccount("kiseok")
    void closeStudy()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        study.setClosed(false);
        study.setPublished(true);
        assertThat(study.isClosed()).isFalse();

        mockMvc.perform(post("/study/test-path/settings/study/close")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertThat(study.isClosed()).isTrue();
    }


    @Test
    @DisplayName("스터디 경로 변경")
    @WithAccount("kiseok")
    void changePath()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);

        mockMvc.perform(post("/study/test-path/settings/study/path")
                .param("newPath", "new-path")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        assertThat(study.getPath()).isEqualTo("new-path");
    }

    @Test
    @DisplayName("스터디 경로 변경 - fail")
    @WithAccount("kiseok")
    void changePath_fail()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        studyFactory.createStudy("new-path", kiseok);

        mockMvc.perform(post("/study/test-path/settings/study/path")
                .param("newPath", "new-path")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().attributeExists("studyPathError"));

        assertThat(study.getPath()).isEqualTo("test-path");
    }

    @Test
    @DisplayName("스터디 title 변경")
    @WithAccount("kiseok")
    void changeTitle()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);

        mockMvc.perform(post("/study/test-path/settings/study/title")
                .param("newTitle", "new-title")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        assertThat(study.getTitle()).isEqualTo("new-title");
    }

    @Test
    @DisplayName("스터디 title 변경 - fail")
    @WithAccount("kiseok")
    void changeTitle_fail()throws Exception {
        Account kiseok = accountRepository.findByNickname("kiseok");
        Study study = studyFactory.createStudy("test-path", kiseok);
        study.setTitle("original");

        mockMvc.perform(post("/study/test-path/settings/study/title")
                .param("newTitle", "40자가 넘어가면 에러입니다. 40자가 넘어가면 에러입니다. 40자가 넘어가면 에러입니다. 40자가 넘어가면 에러입니다.40자가 넘어가면 에러입니다. 40자가 넘어가면 에러입니다.")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/study"))
                .andExpect(model().attributeExists("studyTitleError"));

        assertThat(study.getTitle()).isEqualTo("original");
    }
}