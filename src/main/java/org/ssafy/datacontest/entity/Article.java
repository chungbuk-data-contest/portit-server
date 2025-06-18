package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.ssafy.datacontest.enums.Category;

import java.time.LocalDateTime;

@Entity
@Table(name = "artwork")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artId;

    private String title;
    private String description;

    // 복수 선택
    @Enumerated(EnumType.STRING)
    private Category category;

    private String externalLink;
    private boolean visible;

    private String videoFileUrl;

    private Long userId; // TODO: FK 연결

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Article(String title, String description, Category category, String externalLink, boolean visible, String videoFileUrl, Long userId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.externalLink = externalLink;
        this.visible = visible;
        this.videoFileUrl = videoFileUrl;
        this.userId = userId;
    }
}
