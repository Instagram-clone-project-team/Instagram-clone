package com.project.Instagram.domain.search.repository;

import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.search.entity.RecentSearch;
import com.project.Instagram.domain.search.entity.Search;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long>, RecentSearchRepositoryQuerydsl {
    @Query("SELECT rs.search FROM RecentSearch rs WHERE rs.member.id = :loginId")
    List<Search> findAllByMemberId(@Param("loginId") Long loginId, Pageable pageable);

    Long countAllByMemberId(Long loginId);

    void deleteAllByMemberId(Long memberId);

    Optional<RecentSearch> findByMemberAndSearch(Member member, Search search);

}
