package com.project.Instagram.domain.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;

@Getter
@Setter
public class EditPostRequest {
    @Size(max = 2200, message = "게시물 내용은 최대 2,200자까지 입력 가능합니다.")
    private String content;
    private MultipartFile image;
}
