package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;

public class ImageMapper {

    public static Image toEntity(String imageUrl, Article article) {
        return Image.builder()
                .imageUrl(imageUrl)
                .article(article)
                .build();
    }
}
