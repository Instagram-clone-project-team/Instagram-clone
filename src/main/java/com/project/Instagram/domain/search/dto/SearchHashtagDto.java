package com.project.Instagram.domain.search.dto;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchHashtagDto extends SearchDto {
    private String name;
    private Integer postCount;

    @QueryProjection
    public SearchHashtagDto(String dtype, Hashtag hashtag) {
        super(dtype);
        this.name = hashtag.getTagName();
        this.postCount = hashtag.getCount();
    }
}
