package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.entity.Search;
import com.project.Instagram.domain.search.repository.querydsl.SearchRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchRepository extends JpaRepository<Search, Long>, SearchRepositoryQuerydsl {
    @Query("SELECT s FROM Search s WHERE s.hashtag.tagName LIKE ?1%")
    List<Search> findHashtagsByTextLike(String text);
}
