package com.project.Instagram.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@RedisHash("refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken implements Serializable {


    @Id
    @Indexed
    private String id;

    @Indexed
    private String value;

    @Indexed
    private Long memberId;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long timeout = 5L;

    @Builder
    public RefreshToken(Long memberId, String value){
        this.memberId = memberId;
        this.value = value;
    }
}
