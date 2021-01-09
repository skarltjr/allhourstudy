package com.allhour.allhourstudy.modules.study.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
public class StudyForm {


    @NotBlank
    @Length(min = 2, max = 30)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$")
    private String path; //url

    @NotBlank
    @Length(max = 40)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;
}
