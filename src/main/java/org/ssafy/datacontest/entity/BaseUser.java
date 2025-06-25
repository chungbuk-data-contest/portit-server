package org.ssafy.datacontest.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Setter @Getter
@SuperBuilder
@NoArgsConstructor @AllArgsConstructor
public abstract class BaseUser {
    protected String loginId;
    protected String password;
    protected String phoneNum;
    protected String profileImage;
    protected String role;
    protected String fcmToken;
    protected boolean deleted;
}
