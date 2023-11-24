package com.project.Instagram.global.jwt;

import com.project.Instagram.domain.member.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
    List<RefreshToken> findAllByMemberId(Long memberId);
}
