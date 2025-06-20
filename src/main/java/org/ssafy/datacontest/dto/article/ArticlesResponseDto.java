package org.ssafy.datacontest.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ArticlesResponseDto {
    // 작품 번호, 이미지 썸네일, 제목, 작성자명, 태그List, 카테고리, 만든 날짜
    private Long articleId;
    private String articleTitle;
    // TODO : 작성자명
    private String imageUrl;
    private List<String> tagList;
    private Category category;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
