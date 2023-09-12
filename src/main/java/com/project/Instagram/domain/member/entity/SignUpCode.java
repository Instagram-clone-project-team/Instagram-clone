package com.project.Instagram.domain.member.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Getter
@RedisHash("SignUpCode")
public class SignUpCode implements Serializable {
    @Id
    @Indexed
    private String username;
    private String email;
    private String code;
    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long timeout = 300L;

    @Builder
    public SignUpCode(String username, String email, String code) {
        this.username = username;
        this.email = email;
        this.code = code;
    }
}
