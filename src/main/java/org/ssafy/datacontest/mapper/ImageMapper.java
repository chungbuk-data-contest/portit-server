package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;


public class ImageMapper {

    public static Image toEntity(String fileUrl, Article article, int index) {
        return Image.builder()
                .imageUrl(fileUrl)
                .article(article)
                .imageIndex(index)
                .build();
    }
}
