package com.project.Instagram.domain.search.repository;

import com.project.Instagram.config.TestConfig;
import com.project.Instagram.global.config.QuerydslConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({QuerydslConfig.class, TestConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SearchRepositoryTest {
    @Autowired
    SearchRepository searchRepository;

    // 윤영

    // 동엽

    // 하늘

}