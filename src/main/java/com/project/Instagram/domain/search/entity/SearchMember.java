package com.project.Instagram.domain.search.entity;

import com.project.Instagram.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@DiscriminatorValue("MEMBER")
@Table(name = "search_members")
public class SearchMember extends Search {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public SearchMember(Member member){
        super();
        this.member = member;
    }
}
