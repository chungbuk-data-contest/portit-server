package org.ssafy.datacontest.dto.article;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Valid
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부 new 생성 방지
public class ArticleRequestDto { // 작품 등록 DTO

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    private String description;

    @NotBlank(message = "카테고리는 필수 입력 항목입니다.")
    private String category;

    private String externalLink;

    private boolean visible = true;

    private MultipartFile videoFile;

    private List<String> tag = new ArrayList<>();

    @NotNull
    @Size(min = 1, message = "이미지 파일은 최소 한 개 이상 필요합니다.")
    private List<MultipartFile> imageFiles = new ArrayList<>();

    // TODO: 지워야하는 것
    private Long userId;
}