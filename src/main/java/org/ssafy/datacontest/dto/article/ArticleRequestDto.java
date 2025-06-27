package org.ssafy.datacontest.dto.article;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 new 생성 방지
public class ArticleRequestDto { // 작품 등록 DTO
    // 필수 : 제목, 카테고리, 파일들, 썸네일
    private String title;
    private String description;
    private String category;
    private String externalLink;
    private List<String> tag = new ArrayList<>();
    private List<MultipartFile> files = new ArrayList<>();
    private MultipartFile thumbnail;
}