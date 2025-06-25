package org.ssafy.datacontest.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.repository.custom.ArticleRepositoryCustom;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
    Optional<Article> findByArtId(Long articleId);
    Page<Article> findAll(Pageable pageable);
    List<Article> findByUser_Id(Long userId);

    @Query(value = "SELECT * FROM article WHERE art_id = :id", nativeQuery = true)
    Article findDeletedArticleById(@Param("id") Long id);
}
