package org.ssafy.datacontest.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter @Setter @SuperBuilder
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "deleted = false")
public class User extends BaseUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String nickname;
    private boolean isStudent;

    public void updateUser(String nickname) {
        this.nickname = nickname;
    }
}
