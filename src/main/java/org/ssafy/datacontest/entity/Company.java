package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.ssafy.datacontest.enums.IndustryType;

// DB 저장용
@Entity
@Table(name = "company")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동 생성
public class Company extends BaseUser{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    // 공공데이터에서 받은 값들
    private String companyName;         // 업체명
    private String companyDescription;  // 설명 or 주생산품

    @Enumerated(EnumType.STRING)
    @Column(name = "company_field")
    private IndustryType companyField;  // 업종명

    private String companyLoc;          // 지역
    private Boolean hiring;
    private String companyLink;
}
