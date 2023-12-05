package com.project.Instagram.domain.member.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "사용자 이름엔 대소문자, 숫자만 가능합니다.")
    private String username;

    @NotBlank(message = "인증코드를 입력해주세요")
    @Length(max = 8, min = 8, message = "인증코드는 8자리 입니다.")
    private String code;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 알파벳,숫자 하나이상 들어가야합니다. ")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다.")
    private String newPassword;

}
