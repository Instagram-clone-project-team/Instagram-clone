package com.project.Instagram.domain.member.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Length(max =20, message = "비밀번호는 20문자 이하여야 합니다.")
    private String oldPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상, 알파벳,숫자 하나이상 들어가야합니다. ")
    @Length(max = 20, message = "비밀번호는 20문자 이하여야 합니다.")
    private String newPassword;
}
