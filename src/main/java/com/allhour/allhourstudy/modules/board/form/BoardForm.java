package com.allhour.allhourstudy.modules.board.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class BoardForm {

    @Length(min = 2,max = 50)
    private String title;

    @NotBlank
    private String content;
}
