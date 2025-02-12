package tech.monx.services.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
public class RefreshTokenRedisService {
    @Inject
    RedisDataSource redisDataSource;

    public ValueCommands<String, String> redis;

    @PostConstruct
    private void init() {
        redis = redisDataSource.value(String.class);
    }

}
