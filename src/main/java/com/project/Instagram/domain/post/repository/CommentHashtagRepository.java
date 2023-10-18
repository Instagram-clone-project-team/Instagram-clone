package com.project.Instagram.domain.post.repository;

import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentHashtagRepository extends JpaRepository<CommentHashtag,Long> {
    @Query("Select ch From CommentHashtag ch JOIN FETCH ch.Comment JOIN FETCH ch.Hashtag WHERE ch.hashtag =:hashtag AND ch.comment =:comment")
    CommentHashtag findByCommentAndHashtag(@Param("hashtag") Hashtag hashtag, @Param("comment") Comment comment);
}
