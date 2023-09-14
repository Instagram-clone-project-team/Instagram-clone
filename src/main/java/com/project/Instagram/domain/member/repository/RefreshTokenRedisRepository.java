package com.project.Instagram.domain.member.repository;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByMemberIdAndValue(Long memberId, String value);
}
