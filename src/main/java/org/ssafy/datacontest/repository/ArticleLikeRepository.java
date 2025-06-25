package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ssafy.datacontest.entity.Like;

import java.util.List;
import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByCompany_CompanyIdAndArticle_ArtId(Long articleId, Long companyId);
    List<Like> findByCompany_CompanyId(Long companyId);
    List<Like> findByUser_Id(Long userId);
    Like findByLikeId(Long likeId);
}
