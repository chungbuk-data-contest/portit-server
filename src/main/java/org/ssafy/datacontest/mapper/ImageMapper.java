package org.ssafy.datacontest.mapper;

import org.ssafy.datacontest.dto.image.ImageDto;
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

    public static ImageDto toDto(Image image) {
        return ImageDto.builder()
                .imageId(image.getImageId())
                .imageUrl(image.getImageUrl())
                .imageIndex(image.getImageIndex())
                .build();
    }
}
