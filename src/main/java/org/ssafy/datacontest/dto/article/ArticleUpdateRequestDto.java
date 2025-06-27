package org.ssafy.datacontest.dto.article;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 new 생성 방지
public class ArticleUpdateRequestDto {
    private String title;
    private String description;
    private String category;
    private String externalLink;
    private List<String> tag = new ArrayList<>();
    private List<MultipartFile> files = new ArrayList<>();
    @Setter
    private List<ImageUpdateDto> imageIdList = new ArrayList<>();
    private MultipartFile newThumbnailImage; // 새로 생성된 썸네일이 있을 경우 (새로 등록된 사진도)
    private String thumbnailUrl; // 기존 사진일 경우 url
}
