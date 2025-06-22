package org.ssafy.datacontest.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.ssafy.datacontest.entity.redis.Refresh;

@Repository
public interface RefreshRepository extends CrudRepository<Refresh, String> {
    void deleteById(@NotNull String loginId); // email 기반으로 바로 삭제 가능
    boolean existsById(@NotNull String loginId);
}
