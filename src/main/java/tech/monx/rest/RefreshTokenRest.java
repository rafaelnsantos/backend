package tech.monx.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Path("auth")
@RequestScoped
@Slf4j
public class RefreshTokenRest {
    @Context
    SecurityContext context;

    @GET
    @RolesAllowed("user")
    @Path("test")
    public Response test() {

        return Response.ok(TestDto.builder().message(context.getUserPrincipal().getName()).build()).build();
    }

    @Builder
    static class TestDto {
        public String message;
    }

}
