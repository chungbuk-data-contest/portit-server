package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Tag;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByArticle(Article article);
    void deleteByArticle(Article article);
}
