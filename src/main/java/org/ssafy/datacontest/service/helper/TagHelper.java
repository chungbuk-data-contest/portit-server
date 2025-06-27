package org.ssafy.datacontest.service.helper;

import org.springframework.stereotype.Component;
import org.ssafy.datacontest.dto.article.ArticleUpdateRequest;
import org.ssafy.datacontest.entity.Article;
import org.ssafy.datacontest.entity.Tag;
import org.ssafy.datacontest.mapper.TagMapper;
import org.ssafy.datacontest.repository.TagRepository;

import java.util.List;

@Component
public class TagHelper {

    private final TagRepository tagRepository;

    public TagHelper(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void updateArticleTags(ArticleUpdateRequest dto, Article article) {
        tagRepository.deleteByArticle(article);
        saveTag(dto.getTag(), article);
    }

    public void saveTag(List<String> tags, Article article) {
        for(String tag: tags){
            Tag tagg = TagMapper.toEntity(tag, article);
            tagRepository.save(tagg);
        }
    }
}
