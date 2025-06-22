package org.ssafy.datacontest.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ArticleResponseDto {
    // 유저 정보
    private String userName;
    private String userEmail;

    // 작품 정보
    private Long articleId;
    private String title;
    private String description;
    private List<ImageDto> fileUrl;
    private List<TagDto> tagList;
    private Category category;
    private String externalLink;
    private Long likeCount;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder
    public ArticleResponseDto(String userName, String userEmail, Long articleId, String title, String description, List<ImageDto> fileUrl, List<TagDto> tagList, Category category, String externalLink, Long likeCount, LocalDateTime createdAt) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.articleId = articleId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.tagList = tagList;
        this.category = category;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.externalLink = externalLink;
    }
}