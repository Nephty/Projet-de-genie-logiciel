package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.exception.throwables.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
public class TokenHandler {

    private final String secret = "secret";
    private final Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

    /**
     * @param username username of the user or bank
     * @param issuer path at which the token was created
     * @param role role of the client
     * @param id id of the client
     * @return A map with the access and refresh token
     */
    public Map<String, String> createTokens(String username, String issuer, Role role, String id) {
        if(id == null) {
            log.error("id is null");
        }
        final int maxAccessTokenMinBeforeExp = 60 * 24 * 24;
        final int accessTokenMinBeforeExp = 1;
        String accessToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + maxAccessTokenMinBeforeExp * 60 * 1000))
                .withIssuer(issuer)
                .withClaim(Role.getClaimName(), role.getRole())
                .withClaim("userId", id)
                .sign(algorithm);

        final int refreshTokenMinBeforeExp = 60 * 24 * 14;
        String refreshToken = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenMinBeforeExp * 60 * 1000))
                .withIssuer(issuer)
                .withClaim(Role.getClaimName(), role.getRole())
                .withClaim("userId", id)
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    /**
     * @param authorizationHeader the header with the token of the incoming request
     * @return the decoded jwt if the token is valid
     */
    public DecodedJWT extractToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } else {
            throw new AuthenticationException("Authorization header format incorrect: " + authorizationHeader);
        }
    }
}
