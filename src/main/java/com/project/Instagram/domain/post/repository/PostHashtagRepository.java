package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostHashtagRepository extends JpaRepository<PostHashtag,Long> {
    @Query("SELECT ph FROM PostHashtag ph JOIN FETCH ph.post JOIN FETCH ph.hashtag WHERE ph.hashtag = :hashtag AND ph.post = :post")
    PostHashtag findByPostAndHashtag(@Param("hashtag") Hashtag hashtag, @Param("post") Post post);
}
