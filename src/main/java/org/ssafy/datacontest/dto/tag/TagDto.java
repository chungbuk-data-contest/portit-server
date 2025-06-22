package org.ssafy.datacontest.dto.tag;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagDto {
    private Long tagId;
    private String tagName;

    public TagDto(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }
}