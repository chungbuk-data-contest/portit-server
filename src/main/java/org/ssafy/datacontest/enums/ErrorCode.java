package org.ssafy.datacontest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMPTY_TITLE(HttpStatus.BAD_REQUEST, "EMPTY_TITLE", "제목은 필수 입력 항목입니다."),
    EMPTY_CATEGORY(HttpStatus.BAD_REQUEST, "EMPTY_CATEGORY", "카테고리는 필수 선택 항목입니다."),
    EMPTY_DESCRIPTION(HttpStatus.BAD_REQUEST, "EMPTY_DESCRIPTION", "작품 설명란은 필수 입력 항목입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "EMPTY_FILE", "이미지/영상 파일은 필수 입력 항목입니다."),
    EMPTY_TAG(HttpStatus.BAD_REQUEST, "EMPTY_TAG", "태그는 필수 선택 항목입니다."),
    EMPTY_IMAGE_ID(HttpStatus.BAD_REQUEST, "EMPTY_IMAGE_ID", "이미지/영상 파일 ID는 필수 전달 항목입니다."),
    EMPTY_THUMBNAIL(HttpStatus.BAD_REQUEST, "EMPTY_THUMBNAIL", "썸네일은 필수 전달 항목입니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "INVALID_CATEGORY", "존재하지 않는 카테고리입니다."),
    INVALID_TAG_COUNT(HttpStatus.BAD_REQUEST, "INVALID_TAG_COUNT", "태그는 2개를 초과할 수 없습니다."),
    INVALID_TAG_LENGTH(HttpStatus.BAD_REQUEST, "INVALID_TAG_LENGTH", "태그는 반드시 4자여야 합니다."),
    INVALID_TAG_TYPE(HttpStatus.BAD_REQUEST, "INVALID_TAG_TYPE", "태그는 #을 포함하여 전달해야 합니다."),
    INVALID_THUMBNAIL(HttpStatus.BAD_REQUEST, "INVALID_THUMBNAIL", "썸네일 파일 또는 링크 중 하나는 반드시 전달해야 합니다."),

    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE_NOT_FOUND", "존재하지 않는 작품 번호입니다."),
    FORBIDDEN_ARTICLE_ACCESS(HttpStatus.FORBIDDEN, "FORBIDDEN_ARTICLE_ACCESS", "해당 작품에 대한 권한이 없습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_USER", "해당 작업에 대한 권한이 없습니다."),

    REFRESH_TOKEN_NULL(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_NULL", "Refresh Token이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_EXPIRED", "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_INVALID", "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "REFRESH_TOKEN_NOT_FOUND", "서버에 존재하지 않는 Refresh Token입니다."),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST,"USER_NOT_FOUND","존재하지 않는 유저입니다."),
    EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "EMPTY_EMAIL", "아이디는 필수 입력 항목입니다."),
    DUPLICATED_ID(HttpStatus.BAD_REQUEST, "DUPLICATED_ID", "이미 사용 중인 아이디입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST,"DUPLICATED NICNAME", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "비밀번호는 최소 8자 이상이어야 합니다."),
    EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "EMPTY_NICKNAME", "닉네임은 필수 입력 항목입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "INVALID_PHONE_NUMBER", "전화번호 형식이 올바르지 않습니다."),

    EMPTY_COMPANY_PASSWORD(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_PASSWORD", "비밀번호는 필수 입력 항목입니다."),
    EMPTY_COMPANY_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_PHONE_NUMBER", "전화번호는 필수 입력 항목입니다."),
    EMPTY_COMPANY_NAME(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_NAME", "업체명은 필수 입력 항목입니다."),
    EMPTY_COMPANY_FIELD(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_FIELD", "업종명은 필수 입력 항목입니다."),
    EMPTY_COMPANY_LOCATION(HttpStatus.BAD_REQUEST, "EMPTY_COMPANY_LOCATION", "지역은 필수 입력 항목입니다."),
    NULL_HIRING_STATUS(HttpStatus.BAD_REQUEST, "NULL_HIRING_STATUS", "채용 여부는 필수 항목입니다."),
    INVALID_COMPANY_FIELD(HttpStatus.BAD_REQUEST, "INVALID_COMPANY_FIELD", "존재하지 않는 업종명입니다."),
    INVALID_COMPANY_LOCATION(HttpStatus.BAD_REQUEST, "INVALID_COMPANY_LOCATION", "존재하지 않는 지역명입니다."),

    INVALID_EMAIL_DOMAIN(HttpStatus.BAD_REQUEST, "INVALID_EMAIL_DOMAIN", "ac.kr 도메인의 학교 이메일만 허용됩니다."),

    MISMATCH_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "MISMATCH_IMAGE_COUNT", "이미지ID 리스트와 업로드된 파일 수가 일치하지 않습니다."),

    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_UPLOAD_FAILED", "S3에 파일 업로드 중 오류가 발생했습니다."),
    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST,"COMPANY_NOT_FOUND" ,"기업이 존재하지 않습니다."),

    REGISTERED_ARTICLE_PREMIUM(HttpStatus.BAD_REQUEST, "REGISTERED_ARTICLE_PREMIUM", "이미 프리미엄으로 등록된 게시글입니다."),
    CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "CHATROOM_NOT_FOUND" ,"채팅방이 존재하지 않습니다." ),
    INVALID_USER(HttpStatus.BAD_REQUEST,"INVALID_USER" ,"채팅방에 접근할 수 없는 유저입니다."),
    FCM_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST,"FCM_TOKEN_NOT_FOUND" ,"토큰을 조회할 수 없습니다."),
    PREMIUM_ARTICLE(HttpStatus.BAD_REQUEST,"PREMIUM_ARTICLE" ,"이미 프리미엄 게시글입니다." ),
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST,"PAYMENT_NOT_FOUND" ,"유효한 주문번호가 아닙니다."),
    AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST,"AMOUND_MISMATCH" ,"결제 금액이 일치하지 않습니다." );

    // 프론트에서 message 만을 이용해서 에러를 구분하는 건 유지보수 면에서 좋지 않기에,
    // code를 지정해주어서 클라이언트 측에서 디테일한 핸들링을 하도록 하는 것이 좋다.

    // status: Header로 반환할 HTTP Status Code
    // code: Payload로 반환할 에러 코드
    // message: 에러 코드 문서화를 위한 설명

    public final HttpStatus httpStatus;
    public final String code;
    public final String message;
}
