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
@Builder
public class ArticleDto {

    private Long articleId;
    private String title;
    private String description;
    private String externalLink;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Category category;

    private List<ImageDto> images;
    private List<TagDto> tags;
}
