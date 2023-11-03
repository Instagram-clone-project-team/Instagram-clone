package com.project.Instagram.domain.post.entity;

import com.project.Instagram.global.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "hashtags")
public class Hashtag extends BaseTimeEntity {

    @Id
    @Column(name = "hashtag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hashtag_name")
    private String tagName;

    @Column(name = "hashtag_count")
    private Integer count;

    @Builder
    public Hashtag(String tagName){
        this.tagName = tagName;
        this.count = 1;
    }

    public void updatecount(int num){
        this.count = count+num;
    }


}
