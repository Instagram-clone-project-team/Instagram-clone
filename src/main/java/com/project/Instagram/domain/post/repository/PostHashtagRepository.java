package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag,Long> {
}
