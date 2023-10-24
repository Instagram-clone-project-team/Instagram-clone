package com.project.Instagram.domain.comment.repository;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.comment.entity.CommentHashtag;
import com.project.Instagram.domain.post.entity.Hashtag;
import com.project.Instagram.domain.post.entity.Post;
import com.project.Instagram.domain.post.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentHashtagRepository extends JpaRepository<CommentHashtag,Long> {

    CommentHashtag findByHashtagAndComment(@Param("hashtag") Hashtag hashtag, @Param("comment") Comment comment);
}
