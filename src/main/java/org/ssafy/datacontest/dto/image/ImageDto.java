package org.ssafy.datacontest.dto.image;

import lombok.Builder;
import lombok.Getter;

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
