package com.project.Instagram.domain.member.repository;

import com.project.Instagram.domain.member.entity.ResetPasswordCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ResetPasswordCodeRedisRepository extends CrudRepository<ResetPasswordCode, String> {
    Optional<ResetPasswordCode> findByUsername(String username);
}
