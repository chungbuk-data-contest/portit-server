package org.ssafy.datacontest.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ssafy.datacontest.dto.article.*;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.ImageMapper;
import org.ssafy.datacontest.mapper.TagMapper;
import org.ssafy.datacontest.repository.ArticleRepository;
import org.ssafy.datacontest.repository.ImageRepository;
import org.ssafy.datacontest.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;
    private final S3FileService s3FileService;

    @Transactional
    public ArticleResponseDto registerArtwork(ArticleRequestDto articleRequestDto) {
        // TODO: 유저 인증
        log.info(articleRequestDto.getCategory());

        // 이미지, 영상 업로드
        String videoUrl = null;
        if(articleRequestDto.getVideoFile() != null){
            videoUrl = uploadFile(articleRequestDto.getVideoFile());
        }

        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile imageFile : articleRequestDto.getImageFiles()){
            String imageUrl = uploadFile(imageFile);
            imageUrls.add(imageUrl);
        }

        // 글 DB 등록(영상 포함) -> 글 번호 반환
        Article article = ArticleMapper.toEntity(articleRequestDto, videoUrl);
        articleRepository.save(article);

        // 글 번호 가지고 태그 DB 등록
        if(articleRequestDto.getTag() != null || !articleRequestDto.getTag().isEmpty()) {
            saveTag(articleRequestDto.getTag(), article);
        }

        // 글 번호 가지고 이미지 DB 등록
        saveImage(imageUrls, article);

        List<ImageDto> imageDtos = imageRepository.findByArticle(article)
                .stream()
                .map(ImageDto::new)
                .toList();

        List<TagDto> tagDtos = tagRepository.findByArticle(article)
                .stream()
                .map(TagDto::new)
                .toList();

        return new ArticleResponseDto(ArticleDto.from(article, imageDtos, tagDtos));
    }

    public String uploadFile(MultipartFile file) {
        return s3FileService.uploadFile(file);
    }

    public void saveTag(List<String> tags, Article article) {
        for(String tag: tags){
            Tag tagg = TagMapper.toEntity(tag, article);
            tagRepository.save(tagg);
        }
    }

    public void saveImage(List<String> imageUrls, Article article) {
        for(String imageUrl : imageUrls){
            Image imagee = ImageMapper.toEntity(imageUrl, article);
            imageRepository.save(imagee);
        }
    }
}
