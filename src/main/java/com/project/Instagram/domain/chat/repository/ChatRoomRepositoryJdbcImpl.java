package com.project.Instagram.domain.chat.repository;

import com.project.Instagram.domain.chat.entity.ChatRoom;
import com.project.Instagram.domain.chat.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class ChatRoomRepositoryJdbcImpl implements ChatRoomRepositoryJdbc{

    private final JdbcTemplate jdbcTemplate;


    @Override
    public void saveAllBatch(List<ChatRoom> chatRooms, Message message) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final String sql =
                "INSERT INTO join_rooms (`join_room_created_date`, `member_id`, `message_id`, `room_id`) " +
                        "VALUES(?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, now);
                        ps.setString(2, chatRooms.get(i).getMember().getId().toString());
                        ps.setString(3, message.getId().toString());
                        ps.setString(4, chatRooms.get(i).getRoom().getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return chatRooms.size();
                    }
                });
    }

    @Override
    public void updateAllBatch(List<ChatRoom> updateChatRooms, Message message) {
        final String sql = "UPDATE join_rooms SET message_id = ? where join_room_id = ?";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, message.getId().toString());
                        ps.setString(2, updateChatRooms.get(i).getId().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return updateChatRooms.size();
                    }
                });
    }
}
