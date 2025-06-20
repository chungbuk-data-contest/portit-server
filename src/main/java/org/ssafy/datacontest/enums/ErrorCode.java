package org.ssafy.datacontest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "001_EMPTY_TITLE", "제목은 필수 입력 항목입니다."),
    EMPTY_CATEGORY(HttpStatus.BAD_REQUEST, "002_EMPTY_CATEGORY", "카테고리는 필수 선택 항목입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "003_EMPTY_FILE", "이미지/영상 파일은 필수 입력 항목입니다."),
    EMPTY_TAG(HttpStatus.BAD_REQUEST, "004_EMPTY_TAG", "태그는 필수 선택 항목입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "005_INVALID_CATEGORY", "존재하지 않는 카테고리입니다."),
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "006_ARTICLE_NOT_FOUND", "존재하지 않는 작품 번호입니다."),
    FORBIDDEN_ARTICLE_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_ARTICLE_ACCESS", "해당 작품에 대한 권한이 없습니다."),
    REFRESH_TOKEN_NULL(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_NULL", "Refresh Token이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_EXPIRED", "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_INVALID", "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_NOT_FOUND", "서버에 존재하지 않는 Refresh Token입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST,"USER_NOT_FOUND","존재하지 않는 유저입니다."),
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "EMPTY_EMAIL", "이메일은 필수 입력 항목입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "INVALID_EMAIL", "이메일 형식이 유효하지 않습니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호는 최소 8자 이상이어야 합니다."),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "EMPTY_NICKNAME", "닉네임은 필수 입력 항목입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "INVALID_PHONE_NUMBER", "전화번호 형식이 올바르지 않습니다."),
    EMPTY_COMPANY_NAME(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_NAME", "업체명은 필수 입력 항목입니다."),
    EMPTY_COMPANY_FIELD(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_FIELD", "업종명은 필수 입력 항목입니다."),
    EMPTY_COMPANY_LOCATION(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_LOCATION", "지역은 필수 입력 항목입니다."),
    NULL_HIRING_STATUS(HttpStatus.BAD_REQUEST, "NULL_HIRING_STATUS", "채용 여부는 필수 항목입니다."),
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
