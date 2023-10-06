package com.project.Instagram.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PostCreateRequest {

    @NotBlank
    @Pattern(regexp = "^.{1,100}$",
            message = "한 글자 이상, 100 글자 이하로 입력해주세요.")
    private String content;
    private MultipartFile image;
}
