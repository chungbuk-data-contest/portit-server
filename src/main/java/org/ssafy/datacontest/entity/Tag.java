package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_id")
    private Article article;

    private String tagName;

    @Builder
    public Tag(String tagName, Article article) {
        this.tagName = tagName;
        this.article = article;
    }
}
