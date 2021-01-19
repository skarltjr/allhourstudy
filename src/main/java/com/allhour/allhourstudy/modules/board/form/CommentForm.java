package com.allhour.allhourstudy.modules.board.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class CommentForm {

    @Length(max = 200)
    private String chat;

}
