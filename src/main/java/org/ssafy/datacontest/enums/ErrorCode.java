package org.ssafy.datacontest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "", ""),
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
