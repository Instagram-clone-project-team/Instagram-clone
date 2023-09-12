package com.project.Instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "사용자 이름에는 대소문자, 숫자만 사용할 수 있습니다.")
    private String username;

    @NotBlank(message = "이름을 입력해주세요.")
    @Length(min = 2, max = 12, message = "이름은 2문자 이상 12문자 이하여야 합니다.")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Length(max = 20, message = "비밀번호는 20문자 이상이여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일의 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "이메일 인증코드를 입력해주세요.")
    @Length(max = 6, min = 6, message = "인증코드는 6자리 입니다.")
    private String code;

}
