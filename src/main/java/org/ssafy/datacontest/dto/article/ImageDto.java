package org.ssafy.datacontest.dto.article;

import lombok.Getter;
import org.ssafy.datacontest.entity.Image;

@Getter
public class ImageDto {
    private Long imageId;
    private String imageUrl;

    public ImageDto(Image image) {
        this.imageId = image.getImageId();
        this.imageUrl = image.getImageUrl();
    }
}
