package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p join fetch p.member where p.id = :postId")
    Optional<Post> findWithMemberById(Long postId);
}
