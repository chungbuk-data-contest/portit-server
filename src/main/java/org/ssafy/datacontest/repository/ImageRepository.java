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

    @Query(value = """
    SELECT i.art_id, i.image_url
    FROM image i
    INNER JOIN (
        SELECT art_id, MIN(`image_index`) AS min_index
        FROM image
        WHERE art_id IN (:articleIds)
        GROUP BY art_id
    ) first ON i.art_id = first.art_id AND i.image_index = first.min_index
    """, nativeQuery = true)
    List<Object []> findFirstImageUrlsByArticleIds(@Param("articleIds") List<Long> articleIds);

}
