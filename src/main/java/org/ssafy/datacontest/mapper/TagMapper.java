package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.tag.TagDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Tag;

public class TagMapper {

    public static Tag toEntity(String tag, Article article) {
        return Tag.builder()
                .tagName(tag)
                .article(article)
                .build();
    }

    public static TagDto toDto(Tag tag) {
        return TagDto.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .build();
    }
}
