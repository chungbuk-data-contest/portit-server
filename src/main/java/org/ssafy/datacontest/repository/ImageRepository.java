package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByArticle(Article article);
    List<Image> findByImageIdIn(List<Long> imageIdList);
    void deleteByArticle(Article article);
    void deleteByImageId(Long imageId);
}
