package org.ssafy.datacontest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "", ""), // 예시
    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "001_EMPTY_TITLE", "제목은 필수 입력 항목입니다."),
    EMPTY_CATEGORY(HttpStatus.BAD_REQUEST, "002_EMPTY_CATEGORY", "카테고리는 필수 선택 항목입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "003_EMPTY_FILE", "이미지/영상 파일은 필수 입력 항목입니다."),
    EMPTY_TAG(HttpStatus.BAD_REQUEST, "004_EMPTY_TAG", "태그는 필수 선택 항목입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "005_INVALID_CATEGORY", "존재하지 않는 카테고리입니다."),
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "006_ARTICLE_NOT_FOUND", "존재하지 않는 작품 번호입니다."),
    FORBIDDEN_ARTICLE_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_ARTICLE_ACCESS", "해당 작품에 대한 권한이 없습니다.")

    ;

    // 프론트에서 message 만을 이용해서 에러를 구분하는 건 유지보수 면에서 좋지 않기에,
    // code를 지정해주어서 클라이언트 측에서 디테일한 핸들링을 하도록 하는 것이 좋다.

    // status: Header로 반환할 HTTP Status Code
    // code: Payload로 반환할 에러 코드
    // message: 에러 코드 문서화를 위한 설명

    public final HttpStatus httpStatus;
    public final String code;
    public final String message;
}
