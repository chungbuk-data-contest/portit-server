package org.ssafy.datacontest.dto.article;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.entity.Tag;

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