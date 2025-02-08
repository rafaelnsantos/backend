package tech.monx.webauthn;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Date;

@ApplicationScoped
public class CookieService {
    @ConfigProperty(name = "jwt.token.name")
    String tokenName;

    @ConfigProperty(name = "jwt.refresh.token.name")
    String refreshTokenName;

    @ConfigProperty(name = "jwt.refresh.token.expiration")
    int refreshTokenExpiration;

    @ConfigProperty(name = "jwt.token.expiration")
    int tokenExpiration;

    public NewCookie createCookie(String value) {
        return new NewCookie.Builder(tokenName)
                .value(value)
                .sameSite(NewCookie.SameSite.STRICT)
                .secure(true)
                .httpOnly(true)
                .path("/")
                .expiry(Date.from(new Date().toInstant().plusSeconds(tokenExpiration)))
                .build();
    }

    public NewCookie createRefreshCookie(String value) {
        return new NewCookie.Builder(refreshTokenName)
                .value(value)
                .sameSite(NewCookie.SameSite.STRICT)
                .secure(true)
                .httpOnly(true)
                .path("/v1/auth/refresh")
                .expiry(Date.from(new Date().toInstant().plusSeconds(refreshTokenExpiration)))
                .build();
    }

    public Response getClearCookiesResponse () {
        return Response.status(Response.Status.UNAUTHORIZED)
                .header("Set-Cookie", tokenName + "=; HttpOnly; Path=/; Max-Age=0")
                .header("Set-Cookie", refreshTokenName + "=; HttpOnly; Path=/v1/auth/refresh; Max-Age=0")
                .build();
    }

}