package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByArticle(Article article);
    void deleteByArticle(Article article);
}
