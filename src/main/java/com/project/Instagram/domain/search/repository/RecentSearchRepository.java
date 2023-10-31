package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long>, RecentSearchRepositoryQuerydsl {

}
