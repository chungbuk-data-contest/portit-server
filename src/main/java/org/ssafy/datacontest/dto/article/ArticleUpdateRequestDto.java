package org.ssafy.datacontest.dto.article;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

//    public void updateImageIdList(List<ImageUpdateDto> imageIdList) {
//        this.imageIdList = imageIdList;
//    }
}
