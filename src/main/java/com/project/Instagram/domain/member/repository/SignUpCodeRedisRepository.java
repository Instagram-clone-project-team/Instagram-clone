package com.project.Instagram.domain.member.repository;

import com.project.Instagram.domain.member.entity.SignUpCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SignUpCodeRedisRepository extends CrudRepository<SignUpCode, String> {
    Optional<SignUpCode> findByEmail(String email);
}
