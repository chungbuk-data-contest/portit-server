package org.ssafy.datacontest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
