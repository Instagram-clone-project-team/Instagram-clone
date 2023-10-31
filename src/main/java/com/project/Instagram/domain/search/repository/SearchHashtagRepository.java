package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.entity.SearchHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHashtagRepository extends JpaRepository<SearchHashtag, Long> {
}
