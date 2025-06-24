package org.ssafy.datacontest.dto;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.dto.article.ArticlesResponseDto;

import java.util.List;

@Getter
@Builder
public class SliceResponseDto<T> {
    private List<T> content;
    private boolean hasNext;

    public SliceResponseDto(List<T> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;
    }
}
