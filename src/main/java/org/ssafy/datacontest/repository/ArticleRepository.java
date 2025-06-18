package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
