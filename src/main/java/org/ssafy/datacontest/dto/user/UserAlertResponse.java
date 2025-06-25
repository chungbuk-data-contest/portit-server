package org.ssafy.datacontest.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAlertResponse {
    private Long companyId;
    private String companyName;
    private String articleTitle;
    private boolean readed;
}
