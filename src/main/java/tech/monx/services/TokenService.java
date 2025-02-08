package tech.monx.services;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class TokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String ISSUER;

    public String generateGameToken(String id, String gameId) {
        return Jwt.issuer(ISSUER)
                        .subject(id)
                        .groups(Set.of(gameId, "game"))
                        .claim(Claims.address.name(), gameId)
                        .expiresIn(Duration.ofDays(1))
                        .sign();
    }

    public String generateLoginToken(String id) {
        return Jwt.issuer(ISSUER)
                        .subject(id)
                        .groups(Set.of("player"))
                        .expiresIn(Duration.ofDays(1))
                        .sign();
    }
}
