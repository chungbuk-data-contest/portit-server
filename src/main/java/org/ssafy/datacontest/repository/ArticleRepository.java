package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Article;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByArtId(Long articleId);
}
