package tech.monx.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import tech.monx.services.TokenService;

import java.util.UUID;

@Path("auth")
public class AuthResource {

    @Inject
    TokenService tokenService;

    @GET
    public String token() {
        return tokenService.generateLoginToken(UUID.randomUUID().toString());
    }
}
