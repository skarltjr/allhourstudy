package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.study.form.StudyDescriptionForm;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public Study createNewStudy(StudyForm studyForm, Account account) {
        Study study = modelMapper.map(studyForm, Study.class);
        study.getManagers().add(account);
        return studyRepository.save(study);
    }

    public Study getStudy(String path) {
        Study study = studyRepository.findByPath(path);
        checkIfExistingStudy(path, study);
        return study;
    }

    private void checkIfExistingStudy(String path, Study study) {
        if (study == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 존재하지 않습니다");
        }
    }

    private void checkIfIsManager(Study byPath, Account account) {
        if (!byPath.getManagers().contains(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 권한이 없습니다");
        }
    }

    public Study getStudyToUpdate(String path, Account account) {
        Study byPath = studyRepository.findByPath(path);
        checkIfExistingStudy(path, byPath);
        checkIfIsManager(byPath, account);
        return byPath;
    }

    public void updateDescription(StudyDescriptionForm form, Study study) {
        modelMapper.map(form, study);
    }
}
