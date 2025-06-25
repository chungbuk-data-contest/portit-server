package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter @SuperBuilder
@NoArgsConstructor @AllArgsConstructor
public class User extends BaseUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String nickname;
    private boolean isStudent;
    private boolean deleted;

    public void updateUser(String nickname) {
        this.nickname = nickname;
    }

    public void updateDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
