package org.ssafy.datacontest.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.dto.image.ImageDto;
import org.ssafy.datacontest.dto.tag.TagDto;
import org.ssafy.datacontest.enums.Category;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ArticleResponse {
    // 유저 정보
    private String userName;
    private String userLoginId;

    // 작품 정보
    private Long articleId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private List<ImageDto> fileUrl;
    private List<TagDto> tagList;
    private Category category;
    private String externalLink;
    private Long likeCount;
    private boolean premium;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;


    @Builder
    public ArticleResponse(String userName, String userLoginId, Long articleId, String title, String description, String thumbnailUrl, List<ImageDto> fileUrl, List<TagDto> tagList, Category category, String externalLink, Long likeCount, LocalDateTime createdAt, boolean premium) {
        this.userName = userName;
        this.userLoginId = userLoginId;
        this.articleId = articleId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.fileUrl = fileUrl;
        this.tagList = tagList;
        this.category = category;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.externalLink = externalLink;
        this.premium = premium;
    }
}