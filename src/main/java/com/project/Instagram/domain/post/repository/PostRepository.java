package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.member where p.id = :postId")
    Optional<Post> findWithMemberById(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    Page<Post> findAllPostPage(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId AND p.deletedAt IS NULL")
    Page<Post> findMemberAllPostPage(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.member.id IN :memberIds AND p.deletedAt IS NULL")
    Page<Post> findByMemberIds(@Param("memberIds") List<Long> memberIds, Pageable pageable);
}
