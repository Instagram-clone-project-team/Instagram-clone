package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    Page<Post> findAllPostPage(Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId AND p.deletedAt IS NULL")
    Page<Post> findMemberAllPostPage(@Param("memberId") Long memberId, Pageable pageable);

}
