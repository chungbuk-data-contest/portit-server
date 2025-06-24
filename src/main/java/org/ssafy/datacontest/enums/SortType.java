package org.ssafy.datacontest.enums;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum SortType {
    POPULAR(Sort.by(
            Sort.Order.desc("likes"),
            Sort.Order.asc("title")
    )),
    LATEST(Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.asc("title")
    )),
    ;


    // enum 안에 sort 저장 => Sort.by() 반복 생성 피함
    private final Sort sort;
}
