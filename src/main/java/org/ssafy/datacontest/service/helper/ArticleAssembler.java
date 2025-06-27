package org.ssafy.datacontest.service.helper;

import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.article.ArticleListResponse;
import org.ssafy.datacontest.dto.company.CompanyRecommendDto;
import org.ssafy.datacontest.dto.image.ImageDto;
import org.ssafy.datacontest.dto.tag.TagDto;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Company;
import org.ssafy.datacontest.entity.Image;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.mapper.ArticleMapper;
import org.ssafy.datacontest.mapper.CompanyMapper;
import org.ssafy.datacontest.mapper.ImageMapper;
import org.ssafy.datacontest.mapper.TagMapper;
import org.ssafy.datacontest.repository.CompanyRepository;
import org.ssafy.datacontest.repository.ImageRepository;
import org.ssafy.datacontest.repository.TagRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ArticleAssembler {

    private final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final CompanyRepository companyRepository;

    public ArticleAssembler(TagRepository tagRepository, ImageRepository imageRepository, CompanyRepository companyRepository) {
        this.tagRepository = tagRepository;
        this.imageRepository = imageRepository;
        this.companyRepository = companyRepository;
    }

    public List<TagDto> getTagDto(Article article) {
        List<Tag> tagList = tagRepository.findByArticle(article);
        List<TagDto> tagDtos = new ArrayList<>();
        for(Tag tag : tagList){
            tagDtos.add(TagMapper.toDto(tag));
        }

        return tagDtos;
    }

    public List<ImageDto> getImageDto(Article article) {
        List<Image> fileList = imageRepository.findByArticle(article)
                .stream()
                .sorted(Comparator.comparingInt(Image::getImageIndex))
                .toList();

        List<ImageDto> imageDtos = new ArrayList<>();
        for (Image image : fileList) {
            imageDtos.add(ImageMapper.toDto(image));
        }
        return imageDtos;
    }


    public List<CompanyRecommendDto> getRecommendedCompanies(Article article) {
        List<Company> companies = companyRepository.findRandomCompany(article.getIndustryType());
        return companies.stream()
                .map(CompanyMapper::toCompanyRecommendDto)
                .toList();
    }

    public Map<Long, List<String>> getTagMapByArticleIds(List<Long> articleIds) {
        List<Object[]> rawTagData = tagRepository.findTagsByArticleIds(articleIds);

        return rawTagData.stream()
                .collect(Collectors.groupingBy(
                        row -> ((Number) row[0]).longValue(),
                        Collectors.mapping(row -> (String) row[1], Collectors.toList())
                ));
    }

    public List<ArticleListResponse> mapArticlesToDtoList(List<Article> articles, Map<Long, List<String>> tagMap) {
        return articles.stream()
                .map(article -> ArticleMapper.toArticlesResponseDto(
                        article,
                        tagMap.getOrDefault(article.getArtId(), List.of())
                ))
                .toList();
    }
}
