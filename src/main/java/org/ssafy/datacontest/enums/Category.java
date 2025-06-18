package org.ssafy.datacontest.enums;

import lombok.Getter;

@Getter
public enum Category {
    DESIGN("디자인"),
    DEVELOP("개발"),
    VIDEO("영상"),
    PLAN("기획"),
    INDUSTRY("산업/제품");

    private final String message;

    Category(String message) {
        this.message = message;
    }
}
