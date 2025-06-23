package org.ssafy.datacontest.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum IndustryType {
    IT_SOFTWARE("IT/소프트웨어", List.of("정보처리S/W")),
    DESIGN_CONTENTS("디자인/콘텐츠", List.of()),
    MEDIA("영상/방송/미디어", List.of()),
    EDUCATION_EDUTECH("교육/에듀테크", List.of()),
    MEDICAL_HEALTH("의료/헬스케어", List.of()),
    MANUFACTURING_ELECTRONICS("제조/기계/전자", List.of("제조업")),
    COMMERCE("유통/커머스", List.of("도소매업")),
    CONSTRUCTION_REAL_ESTATE("건설/부동산", List.of("건설운수")),
    CULTURE_ART("문화/예술/공연", List.of()),
    ENVIRONMENT_ENERGY("환경/에너지", List.of("기타", "농,어,임,광업")),
    PUBLIC_ORGANIZATION("공공기관/비영리/행정", List.of());

    private final String label;
    private final List<String> aliases;

    IndustryType(String label, List<String> aliases) {
        this.label = label;
        this.aliases = aliases;
    }

    public String getLabel() {
        return label;
    }

    public static IndustryType fromAlias(String input) {
        for (IndustryType type : values()) {
            if (type.aliases.contains(input.trim())) {
                return type;
            }
        }
        return IT_SOFTWARE; // 매칭 안 되면 기본값 처리
    }
}