package tech.monx.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import tech.monx.services.TokenService;
import tech.monx.webauthn.CookieService;

@Path("auth")
@RequestScoped
@Slf4j
public class RefreshTokenRest {
    @Inject
    @Claim(standard = Claims.upn)
    String deviceId;

    @Inject
    JsonWebToken jwt;

    @Inject
    @Claim(standard = Claims.sub)
    String userId;

    @Inject
    TokenService tokenService;

    @Inject
    CookieService cookieService;

    @Path("/refresh")
    @GET
    @Transactional
    @RolesAllowed({"refresh"})
    public Response refresh() {
        var tokens = tokenService.refreshToken(userId, deviceId, jwt.getRawToken());
        return Response.ok(tokens)
                .cookie(cookieService.createCookie(tokens.getToken()))
                .cookie(cookieService.createRefreshCookie(tokens.getRefreshToken()))
                .build();
    }

}
