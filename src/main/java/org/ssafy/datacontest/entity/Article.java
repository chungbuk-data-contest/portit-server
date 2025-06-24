package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.ssafy.datacontest.enums.Category;
import org.ssafy.datacontest.enums.IndustryType;

import java.time.LocalDateTime;

@Entity
@Table(name = "Article")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String externalLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long likeCount = 0L;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String thumbnailUrl;
    @Setter
    private boolean premium;

    @Enumerated(EnumType.STRING)
    private IndustryType industryType;

    @Builder
    public Article(String title, String description, String externalLink, Category category, User user, String thumbnailUrl, boolean premium, IndustryType industryType) {
        this.title = title;
        this.description = description;
        this.externalLink = externalLink;
        this.category = category;
        this.user = user;
        this.thumbnailUrl = thumbnailUrl;
        this.premium = premium;
        this.industryType = industryType;
    }

    public void updateArticle(String title, String description, String externalLink, Category category, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.externalLink = externalLink;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void updatePremium(boolean premium) {
        this.premium = premium;
    }
}
