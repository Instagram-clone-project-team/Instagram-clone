package com.project.Instagram.domain.alarm.repository;

import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;

import java.util.List;

public interface AlarmRepositoryJdbc {
    void saveMentionPostAlarms(Member agent, List<Member> targets, Post post);

    void saveMentionCommentAlarms(Member agent, List<Member> targets, Post post, Comment comment);
}
