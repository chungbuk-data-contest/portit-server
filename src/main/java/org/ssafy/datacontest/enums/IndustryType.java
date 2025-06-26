package org.ssafy.datacontest.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.ssafy.datacontest.exception.CustomException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
public enum IndustryType {
    IT_SOFTWARE("IT/소프트웨어", List.of("정보처리S/W", "연구개발서비스")),
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

    public static IndustryType fromAlias(String input) {
        String trimmedInput = input.trim();

        return Arrays.stream(values())
                .filter(type -> type.label.equalsIgnoreCase(trimmedInput)
                        || type.name().equalsIgnoreCase(trimmedInput)
                        || type.aliases.contains(trimmedInput))
                .findFirst()
                .orElseThrow(() -> {
                    log.info(input);
                    return new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_COMPANY_FIELD);
                });
    }
}