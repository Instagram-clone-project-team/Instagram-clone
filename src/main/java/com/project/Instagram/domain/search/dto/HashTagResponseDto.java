package com.project.Instagram.domain.search.dto;

import com.project.Instagram.domain.post.entity.Hashtag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashTagResponseDto {
    private String name;
    private Integer count;

    public static HashTagResponseDto HashTagConvertToReseponseDto(Hashtag hashtag) {
        return new HashTagResponseDto(hashtag.getTagName(),hashtag.getCount());
    }
}
