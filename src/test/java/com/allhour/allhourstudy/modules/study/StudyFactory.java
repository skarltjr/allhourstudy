package com.allhour.allhourstudy.modules.study;

import com.allhour.allhourstudy.modules.account.Account;
import com.allhour.allhourstudy.modules.study.form.StudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyFactory {

    private final StudyRepository studyRepository;
    private final StudyService studyService;

    public Study createStudy(String path, Account manager) {
        StudyForm study = new StudyForm();
        study.setPath(path);
        Study newStudy = studyService.createNewStudy(study, manager);
        return newStudy;
    }


}
