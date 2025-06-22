package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_id") // 실제 DB 컬럼명
    private Article article;

    private String imageUrl;

    private int imageIndex;

    @Builder
    public Image(String imageUrl, int imageIndex, Article article) {
        this.imageUrl = imageUrl;
        this.article = article;
        this.imageIndex = imageIndex;
    }

    public void updateImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }
}
