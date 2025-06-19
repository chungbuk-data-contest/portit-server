package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.ssafy.datacontest.enums.Category;

import java.time.LocalDateTime;

@Entity
@Table(name = "article")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String externalLink;

//    private Long userId; // TODO: FK 연결

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Article(String title, String description, String externalLink, Category category) {
        this.title = title;
        this.description = description;
        this.externalLink = externalLink;
        this.category = category;
    }
}
