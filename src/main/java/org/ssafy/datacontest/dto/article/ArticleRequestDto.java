package org.ssafy.datacontest.dto.article;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 new 생성 방지
public class ArticleRequestDto { // 작품 등록 DTO
    // 필수
    private String title;
    private String description;

    // 필수
    private String category;
    private String externalLink;

    // 필수
    private List<String> tag = new ArrayList<>();

    // 필수
    private List<MultipartFile> files = new ArrayList<>();
}