package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

}
