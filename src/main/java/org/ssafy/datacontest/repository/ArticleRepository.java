package org.ssafy.datacontest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Article;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
    Optional<Article> findByArtId(Long articleId);
    Page<Article> findAll(Pageable pageable);
    List<Article> getAllByCategory(String category);
}
