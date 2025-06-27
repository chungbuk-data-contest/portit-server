package org.ssafy.datacontest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByLoginId(String email);

    boolean existsByLoginId(String email);

    boolean existsByNickname(String nickname);

    boolean existsByPhoneNum(String phoneNum);
}
