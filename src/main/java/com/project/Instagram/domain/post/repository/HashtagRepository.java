package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface HashtagRepository extends JpaRepository<Hashtag,Long> {
    List<Hashtag> findByTagNameIn(Set<String> tagNames);
}
