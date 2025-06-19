package org.ssafy.datacontest.dto.article;

import lombok.Getter;
import org.ssafy.datacontest.entity.Tag;

@Getter
public class TagDto {
    private Long tagId;
    private String tagName;

    public TagDto(Tag tag) {
        this.tagId = tag.getTagId();
        this.tagName = tag.getTagName();
    }
}