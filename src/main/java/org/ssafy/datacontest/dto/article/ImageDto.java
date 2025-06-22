package org.ssafy.datacontest.dto.article;

import lombok.Builder;
import lombok.Getter;
import org.ssafy.datacontest.entity.Image;

@Getter
@Builder
public class ImageDto {
    private Long imageId;
    private String imageUrl;
    private int imageIndex;

    public ImageDto(Long imageId, String imageUrl, int imageIndex) {
        this.imageId = imageId;
        this.imageUrl = imageUrl;
        this.imageIndex = imageIndex;
    }
}
