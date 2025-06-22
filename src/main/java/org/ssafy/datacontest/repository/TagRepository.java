package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Tag;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByArticle(Article article);
    void deleteByArticle(Article article);

    @Query(value = """
    SELECT t.art_id, t.tag_name
    FROM tag t 
    WHERE t.art_id IN :articleIds
        """, nativeQuery = true)
    List<Object []> findTagsByArticleIds(@Param("articleIds") List<Long> articleIds);
}
