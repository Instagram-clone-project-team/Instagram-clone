package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long>, SearchRepositoryQuerydsl {
}
