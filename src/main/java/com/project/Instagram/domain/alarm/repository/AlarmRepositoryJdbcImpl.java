package com.project.Instagram.domain.alarm.repository;

import com.project.Instagram.domain.alarm.dto.AlarmType;
import com.project.Instagram.domain.comment.entity.Comment;
import com.project.Instagram.domain.member.entity.Member;
import com.project.Instagram.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class AlarmRepositoryJdbcImpl implements AlarmRepositoryJdbc{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveMentionPostAlarms(Member agent, List<Member> targets, Post post) {
        final String sql =
                "INSERT INTO alarms (`alarm_type`, `alarm_agent_id`, `post_id`, `alarm_target_id`) " +
                        "VALUES(?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, AlarmType.MENTION_POST.name());
                        ps.setString(2, agent.getId().toString());
                        ps.setString(3, post.getId().toString());
                        ps.setString(4, targets.get(i).getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return targets.size();
                    }
                });
    }

    @Override
    public void saveMentionCommentAlarms(Member agent, List<Member> targets, Post post, Comment comment) {
        final String sql =
                "INSERT INTO alarms (`alarm_type`, `alarm_agent_id`, `comment_id`, `post_id`, `alarm_target_id`)" +
                        "VALUES(?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, AlarmType.MENTION_COMMENT.name());
                        ps.setString(2, agent.getId().toString());
                        ps.setString(3, comment.getId().toString());
                        ps.setString(4, post.getId().toString());
                        ps.setString(5, targets.get(i).getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return targets.size();
                    }
                });
    }
}
