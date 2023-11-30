package com.project.Instagram.domain.search.entity;

import com.project.Instagram.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "recent_searches")
@IdClass(RecentSearch.RecentSearchId.class)
public class RecentSearch {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "search_id")
    private Search search;

    @Column(name = "last_searched_date")
    private LocalDateTime lastSearchedDate;

    @NoArgsConstructor
    @AllArgsConstructor
    static class RecentSearchId implements Serializable {
        private Long member;
        private Long search;
    }
    @Builder
    public RecentSearch(Member member, Search search) {
        this.member = member;
        this.search = search;
    }
    public void updateLastSearchedDate(){
        this.lastSearchedDate = LocalDateTime.now();
    }
}

