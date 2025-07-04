package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.ssafy.datacontest.dto.company.CompanyUpdateRequest;
import org.ssafy.datacontest.enums.IndustryType;
import org.ssafy.datacontest.enums.RegionType;

@Entity
@Table(name = "company")
@Getter @Setter
@SuperBuilder
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동 생성
@Where(clause = "deleted = false")
public class Company extends BaseUser{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    private String companyName;         // 업체명
    private String companyDescription;  // 설명 or 주생산품

    @Enumerated(EnumType.STRING)
    @Column(name = "company_field")
    private IndustryType companyField;  // 업종명

    @Enumerated(EnumType.STRING)
    @Column(name = "company_loc")
    private RegionType companyLoc;      // 지역
    private String simpleAddress;       // 간단 주소
    private Boolean hiring;
    private String companyLink;

    public void updateCompany(CompanyUpdateRequest request) {
        this.companyName = request.getCompanyName();
        this.hiring = request.getHiring();
        this.companyDescription = request.getCompanyDescription();
        this.companyField = IndustryType.fromAlias(request.getCompanyField());
        this.companyLoc = RegionType.fromAlias(request.getCompanyLoc());
        this.companyLink = request.getCompanyLink();
    }
}
