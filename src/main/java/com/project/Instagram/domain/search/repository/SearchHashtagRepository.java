package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.search.entity.SearchHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchHashtagRepository extends JpaRepository<SearchHashtag, Long> {
    void deleteByHashtag(Hashtag hashtag);

    Optional<SearchHashtag> findByHashtagTagName(String tagName);
}
