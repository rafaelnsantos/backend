package tech.monx.webauthn;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkus.security.webauthn.WebAuthnCredentialRecord;
import io.quarkus.security.webauthn.WebAuthnLoginResponse;
import io.quarkus.security.webauthn.WebAuthnRegisterResponse;
import io.quarkus.security.webauthn.WebAuthnSecurity;
import io.vertx.ext.web.RoutingContext;
import tech.monx.services.TokenService;

@Path("webauthn")
@Slf4j
public class WebAuthnRest {

    @Inject
    WebAuthnSecurity webAuthnSecurity;

    @Inject
    TokenService tokenService;

    @Inject
    CookieService cookieService;

    // Provide an alternative implementation of the /q/webauthn/login endpoint
    @Path("/login")
    @POST
    @Transactional
    public Response login(@BeanParam WebAuthnLoginResponse webAuthnResponse,
                          RoutingContext ctx) {
        // Input validation
        if (!webAuthnResponse.isSet() || !webAuthnResponse.isValid()) {
            log.info("invalid input !webAuthnResponse.isSet(): {} !webAuthnResponse.isValid(): {}", !webAuthnResponse.isSet(), !webAuthnResponse.isValid());
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            WebAuthnCredentialRecord credentialRecord = this.webAuthnSecurity.login(webAuthnResponse, ctx).await().indefinitely();
            User user = User.findByUsername(credentialRecord.getUsername());
            if (user == null) {
                // Invalid user
                log.info("User does not exist");
                return Response.status(Status.BAD_REQUEST).build();
            }
            // bump the auth counter
            user.webAuthnCredential.counter = credentialRecord.getCounter();
            // make a login cookie
            this.webAuthnSecurity.rememberUser(credentialRecord.getUsername(), ctx);

            return createTokenResponse(user.id.toString());
        } catch (Exception ignored) {
            // handle login failure - make a proper error response
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    // Provide an alternative implementation of the /q/webauthn/register endpoint
    @Path("/register")
    @POST
    @Transactional
    public Response register(@RestForm String username,
                             @BeanParam WebAuthnRegisterResponse webAuthnResponse,
                             RoutingContext ctx) {
        log.info("Registering {}", username);
        // Input validation
        if (username == null || username.isEmpty()
                || !webAuthnResponse.isSet() || !webAuthnResponse.isValid()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        User user = User.findByUsername(username);
        if (user != null) {
            // Duplicate user
            return Response.status(Status.BAD_REQUEST).build();
        }
        try {
            // store the user
            WebAuthnCredentialRecord credentialRecord =
                    webAuthnSecurity.register(username, webAuthnResponse, ctx).await().indefinitely();
            User newUser = new User(credentialRecord.getUsername());
            newUser.username = credentialRecord.getUsername();
            WebAuthnCredential credential =
                    new WebAuthnCredential(credentialRecord, newUser);
            credential.persist();
            newUser.persist();
            log.info("aqi {}", credentialRecord.getCredentialID());
            // make a login cookie
            this.webAuthnSecurity.rememberUser(newUser.username, ctx);

            return createTokenResponse(newUser.id.toString());
        } catch (Exception ignored) {
            // handle login failure
            // make a proper error response
            return Response.status(Status.BAD_REQUEST).build();
        }
    }



    private Response createTokenResponse(String id) {
        var tokens = tokenService.generateAuthTokens(id);

        return Response.ok(tokens)
                .cookie(cookieService.createCookie(tokens.getToken()))
                .cookie(cookieService.createRefreshCookie(tokens.getRefreshToken()))
                .build();
    }


    private Response clearTokenResponse() {
        return Response.status(Response.Status.UNAUTHORIZED)
                .header("Set-Cookie", "token=; HttpOnly; Path=/; Max-Age=0")
                .header("Set-Cookie", "refreshToken=; HttpOnly; Path=/v1/auth/refresh; Max-Age=0")
                .build();
    }


}