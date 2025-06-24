package org.ssafy.datacontest.dto.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.SortType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleScrollRequestDto {
    // 정렬기준에 따라 어떤 커서 필드 사용할지 알 수 있어야 하기에, sortType도 받기
    private SortType sortType = SortType.LATEST;

    private Long lastLikes;
    private String lastTitle;
    private LocalDateTime lastUpdateTime;

    private int size = 10;

    private List<Category> category;
    private String keyword;
    private Boolean isFirstPage = false;
}
