package org.ssafy.datacontest.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("refresh")
@AllArgsConstructor
@Getter
public class Refresh {
    @Id
    private String email;
    private String refresh;

    @TimeToLive
    private Long expiration;
}
