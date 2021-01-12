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

import static com.allhour.allhourstudy.modules.study.form.StudyForm.VALID_PATH_PATTERN;

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
        Study study = studyRepository.findStudyWithTagsByPath(path);
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
        Study study = studyRepository.findWithZonesByPath(path);
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

    public void publish(Study study) {
        study.publish();
    }

    public void close(Study study) {
        study.close();
    }

    public void startRecruit(Study study) {
        study.setRecruiting(true);
    }

    public void stopRecruit(Study study) {
        study.setRecruiting(false);
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }
        if (studyRepository.existsByPath(newPath)) {
            return false;
        }
        return true;
    }

    public void updatePath(Study study, String newPath) {
        study.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 40;
    }

    public void updateTitle(Study study, String newTitle) {
        study.setTitle(newTitle);

    }

    public Study getStudyToUpdateStatus(String path,Account account) {
        Study study = studyRepository.findStudyWithManagersByPath(path);
        checkIfExistingStudy(path, study);
        checkIfIsManager(study, account);
        return study;
    }

    public void remove(Study study) {
        if (study.isRemovable()) {
            studyRepository.delete(study);
        } else {
            throw new IllegalArgumentException("스터디를 삭제할 수 없습니다.");
        }
    }

    public void memberJoin(Study study, Account account) {
        study.memberJoin(account);
    }

    public void memberDisjoin(Study study, Account account) {
        study.memberDisjoin(account);
    }

    public Study getStudyToEnroll(String path) {
        Study study = studyRepository.findWithAllMemberByPath(path);
        checkIfExistingStudy(path,study);
        return study;
    }
}
