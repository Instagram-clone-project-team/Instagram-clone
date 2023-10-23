package com.project.Instagram.domain.member.repository;

import com.project.Instagram.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
    Member findByUsernameOrEmail(String username, String email);
    Page<Member> findAllByDeletedAtIsNull(Pageable pageable);
    List<Member> findAllByUsernameIn(Collection<String> usernames);
}
