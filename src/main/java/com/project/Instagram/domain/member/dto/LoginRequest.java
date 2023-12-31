package com.project.Instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "사용자 이름엔 대소문자, 숫자만 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Length(max = 20, message = "비밀번호는 20문자 이상이여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 최소 하나의 문자와 숫자가 필요합니다.")
    private String password;
}
