package com.project.Instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class UpdateAccountRequest {
    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Length(min = 4, max = 12, message = "사용자 이름은 4문자 이상 12문자 이하여야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]+$", message = "사용자 이름에는 대소문자, 숫자만 사용할 수 있습니다.")
    private String username;
    @NotBlank(message = "이름을 입력해주세요.")
    @Length(min = 2, max = 12, message = "이름은 2문자 이상 12문자 이하여야 합니다.")
    private String name;
    @URL(message = "URL 형식이 맞지 않습니다")
    private String link;

    private String introduce;
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일의 형식이 올바르지 않습니다.")
    private String email;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 양식이 맞지 않습니다")
    private String phone;
    @Pattern(regexp = "^MALE|FEMALE|PRIVATE$", message = "올바르지 않는 성별입니다")
    @NotBlank(message = "성별을 입력해주세요")
    private String gender;

}
