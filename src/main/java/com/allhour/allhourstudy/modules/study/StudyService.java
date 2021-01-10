package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.study.form.StudyDescriptionForm;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import com.allhour.allhourstudy.modules.tag.Tag;
import com.allhour.allhourstudy.modules.zone.Zone;
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

    public void useBanner(Study study) {
        study.setUseBanner(true);
    }

    public void unUsedBanner(Study study) {
        study.setUseBanner(false);
    }

    public void updateBanner(Study study, String image) {
        study.setImage(image);
    }

    public Study getStudyWithTags(Account account, String path) {
        Study study = studyRepository.findWithTagsAndManagersByPath(path);
        checkIfExistingStudy(path, study);
        checkIfIsManager(study, account);
        return study;
    }

    public void addTag(Study study,Tag tag) {
        study.getTags().add(tag);
    }

    public void removeTag(Study study, Tag tag) {
        study.getTags().remove(tag);
    }

    public Study getStudyWithZones(Account account, String path) {
        Study study = studyRepository.findWithZonesAndManagersByPath(path);
        checkIfExistingStudy(path, study);
        checkIfIsManager(study, account);
        return study;
    }

    public void addZone(Study study, Zone zone) {
        study.getZones().add(zone);
    }

    public void removeZone(Study study, Zone zone) {
        study.getZones().remove(zone);
    }
}
