package org.ssafy.datacontest.service.helper;

import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.article.ArticleUpdateRequestDto;
import org.ssafy.datacontest.dto.image.ImageUpdateDto;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.repository.ImageRepository;
import org.ssafy.datacontest.service.S3FileService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ImageHelper {

    private final ImageRepository imageRepository;
    private final S3FileService s3FileService;

    public ImageHelper(ImageRepository imageRepository, S3FileService s3FileService) {
        this.imageRepository = imageRepository;
        this.s3FileService = s3FileService;
    }

    public void updateImages(ArticleUpdateRequestDto dto, List<Image> existingImages) {
        // 삭제된 이미지 찾아서 삭제
        deleteImagesNotInRequest(dto, existingImages);
        // 기존에 남아있는 이미지 -> 순서 바뀐 경우 수정
        updateImageOrder(dto);
    }

    private void deleteImagesNotInRequest(ArticleUpdateRequestDto articleRequestDto, List<Image> existingFile) {
        // imageIdList에서 null이 아닌 id만 추출해서 Set으로 변환
        Set<Long> incomingIds = articleRequestDto.getImageIdList().stream()
                .map(ImageUpdateDto::getImageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 기존 이미지 중 요청에 포함되지 않은 이미지만 삭제 대상
        List<Image> toDeleteImage = existingFile.stream()
                .filter(img -> !incomingIds.contains(img.getImageId()))
                .toList();

        // 삭제
        for (Image image : toDeleteImage) {
            imageRepository.deleteByImageId(image.getImageId());
        }
        deleteFile(toDeleteImage);
    }

    private void deleteFile(List<Image> fileUrls) {
        for (Image file : fileUrls) {
            String fileUrl = file.getImageUrl();
            if (fileUrl != null && !fileUrl.isBlank()) {
                s3FileService.deleteFile(fileUrl);
            }
        }
    }

    private void updateImageOrder(ArticleUpdateRequestDto articleRequestDto) {
        // 1. 유효한 imageId만 필터링해서 DB 조회
        List<Image> existingImages = imageRepository.findByImageIdIn(
                articleRequestDto.getImageIdList().stream()
                        .filter(dto -> dto.getImageId() != null)  // imageId가 null이 아닌 경우만
                        .map(ImageUpdateDto::getImageId)
                        .toList()
        );

        // 2. imageId → Image 매핑
        Map<Long, Image> imageMap = existingImages.stream()
                .collect(Collectors.toMap(Image::getImageId, Function.identity()));

        // 3. 순서 업데이트
        for (int i = 0; i < articleRequestDto.getImageIdList().size(); i++) {
            Long imageId = articleRequestDto.getImageIdList().get(i).getImageId();

            if (imageId == null) continue; // 새 이미지인 경우

            Image image = imageMap.get(imageId);
            if (image != null && image.getImageIndex() != i) {
                image.updateImageIndex(i);
            }
        }
    }
}
