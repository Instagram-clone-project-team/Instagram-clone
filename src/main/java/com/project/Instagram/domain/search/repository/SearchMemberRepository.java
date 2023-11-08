package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.search.entity.SearchMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchMemberRepository extends JpaRepository<SearchMember, Long> {
    Optional<SearchMember> findByMemberUsername(String username);
}
