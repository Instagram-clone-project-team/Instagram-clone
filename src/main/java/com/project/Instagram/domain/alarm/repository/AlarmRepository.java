package com.project.Instagram.domain.alarm.repository;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.alarm.entity.Alarm;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.follow.entity.Follow;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long>, AlarmRepositoryQuerydsl, AlarmRepositoryJdbc {

    void deleteByTypeAndAgentAndTargetAndPost(AlarmType type, Member agent, Member target, Post post);

    void deleteByTypeAndAgentAndTargetAndComment(AlarmType type, Member agent, Member target, Comment comment);

    void deleteByTypeAndAgentAndTargetAndFollow(AlarmType type, Member agent, Member target, Follow follow);

    List<Alarm> findAllByPost(Post post);

    List<Alarm> findAllByCommentIn(List<Comment> comments);
}
