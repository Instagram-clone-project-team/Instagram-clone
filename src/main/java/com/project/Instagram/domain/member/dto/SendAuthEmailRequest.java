package com.project.Instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendAuthEmailRequest {
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "사용자 이름엔 대소문자, 숫자만 가능합니다.")
    private String username;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일의 형식이 올바르지 않습니다.")
    private String email;
}
