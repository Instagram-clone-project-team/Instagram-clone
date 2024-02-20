package com.project.Instagram.domain.chat.entity.mongo;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Document(collection = "chatting")
@Getter
@AllArgsConstructor
@Builder
// MongoDB Chatting 모델
public class Chatting {

    @Id
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderUsername;
    private String content;
    private LocalDateTime sendDate;
    private Long readCount;

}
