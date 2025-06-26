package org.ssafy.datacontest.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.ssafy.datacontest.exception.CustomException;

import java.util.Arrays;
import java.util.List;

@Getter
public enum RegionType {
    SEOUL("서울특별시", List.of("서울")),
    BUSAN("부산광역시", List.of("부산")),
    DAEGU("대구광역시", List.of("대구")),
    INCHEON("인천광역시", List.of("인천")),
    GWANGJU("광주광역시", List.of("광주")),
    DAEJEON("대전광역시", List.of("대전")),
    ULSAN("울산광역시", List.of("울산")),
    SEJONG("세종특별자치시", List.of("세종")),
    GYEONGGI("경기도", List.of("경기")),
    GANGWON("강원도", List.of("강원")),
    CHUNGBUK("충청북도", List.of("충북")),
    CHUNGNAM("충청남도", List.of("충남")),
    JEONBUK("전라북도", List.of("전북")),
    JEONNAM("전라남도", List.of("전남")),
    GYEONGBUK("경상북도", List.of("경북")),
    GYEONGNAM("경상남도", List.of("경남")),
    JEJU("제주특별자치도", List.of("제주"));

    private final String label;
    private final List<String> aliases;

    RegionType(String label, List<String> aliases) {
        this.label = label;
        this.aliases = aliases;
    }

    public static RegionType fromAlias(String input) {
        return Arrays.stream(values())
                .filter(type -> type.label.equalsIgnoreCase(input.trim())
                        || type.name().equalsIgnoreCase(input.trim())
                        || type.aliases.contains(input.trim()))
                .findFirst()
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_COMPANY_LOCATION));
    }
}