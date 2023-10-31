package com.project.Instagram.domain.search.entity;

import com.project.Instagram.domain.post.entity.Hashtag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@DiscriminatorValue("HASHTAG")
@Table(name = "search_hashtags")
public class SearchHashtag extends Search {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

}
