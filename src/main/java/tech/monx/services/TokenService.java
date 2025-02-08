package tech.monx.services;

import io.quarkus.redis.datasource.RedisDataSource;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import tech.monx.webauthn.TokensDao;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@RequestScoped
@Slf4j
public class TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String ISSUER;

    @ConfigProperty(name = "jwt.refresh.token.expiration")
    int refreshTokenExpiration;

    @ConfigProperty(name = "jwt.token.expiration")
    int tokenExpiration;

    @Inject
    RedisDataSource redisDataSource;

    public TokensDao generateAuthTokens(String id) {
        return generateAuthTokens(id, UUID.randomUUID().toString());
    }

    public String generateGameToken(String id, String gameId) {
        return Jwt.issuer(ISSUER)
                .subject(id)
                .groups(Set.of("game"))
                .claim(Claims.address.name(), gameId)
                .expiresIn(Duration.ofSeconds(tokenExpiration))
                .sign();
    }

    public TokensDao refreshToken(String userId, String deviceId, String refreshToken) {
        var redis = redisDataSource.value(String.class);

        var storedRefreshToken = redis.get(deviceId);

        log.info("Stored refresh token: {}", storedRefreshToken);
        log.info("Refresh token: {}", refreshToken);

        if (!refreshToken.equals(storedRefreshToken)) {
            throw new ForbiddenException();
        }

        return generateAuthTokens(userId, deviceId);
    }
    private TokensDao generateAuthTokens(String id, String deviceUUID) {
        log.info("Generating tokens for id: {}", id);

        String token = generateLoginToken(id, deviceUUID);
        String refreshToken = generateRefreshToken(id, deviceUUID);

        var redis = redisDataSource.value(String.class);

        redis.setex(deviceUUID, refreshTokenExpiration ,refreshToken);
        log.info("Saved refresh token");

        return TokensDao.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    private String generateRefreshToken (String id, String device) {
        return Jwt
                .issuer(ISSUER)
                .subject(id)
                .upn(device)
                .groups(Set.of("refresh"))
                .expiresIn(Duration.ofSeconds(refreshTokenExpiration))
                .sign();
    }

    private String generateLoginToken(String id, String device) {
        return Jwt.issuer(ISSUER)
                .subject(id)
                .upn(device)
                .groups(Set.of("user"))
                .expiresIn(Duration.ofSeconds(tokenExpiration))
                .sign();
    }
}

