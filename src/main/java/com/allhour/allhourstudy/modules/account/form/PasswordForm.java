package com.allhour.allhourstudy.modules.account.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class PasswordForm {

    @Length(min = 8,max = 50)
    private String newPassword;

    @Length(min = 8,max = 50)
    private String newPasswordConfirm;
}
