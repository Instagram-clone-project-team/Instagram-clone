package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.entity.SearchMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchMemberRepository extends JpaRepository<SearchMember, Long> {
}
