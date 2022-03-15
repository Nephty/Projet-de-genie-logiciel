package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.exception.throwables.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TokenHandler {

    private final String secret = "secret";
    private final Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

    public Map<String, String> createTokens(String username, String issuer, Role role) {
        final int accessTokenMinBeforeExp = 60;
        String accessToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenMinBeforeExp * 60 * 1000))
                .withIssuer(issuer)
                .withClaim(Role.getClaimName(), role.getRole())
                .sign(algorithm);

        final int refreshTokenMinBeforeExp = 60 * 24 * 14;
        String refreshToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenMinBeforeExp * 60 * 1000))
                .withIssuer(issuer)
                .withClaim(Role.getClaimName(), role.getRole())
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    public DecodedJWT extractToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } else {
            throw new AuthenticationException("Authorization header format incorrect: " + authorizationHeader);
        }
    }

    public void writeErrorToResponse(HttpServletResponse response, Exception e, String authorizationHeader) throws IOException {
        response.setHeader("error", e.getMessage());
        response.setStatus(401);
        Map<String, String> error = new HashMap<>();
        error.put("error_msg", e.getMessage());
        error.put("token", authorizationHeader);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
