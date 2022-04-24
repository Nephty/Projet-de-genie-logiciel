package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.exception.throwables.AuthenticationException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenHandler {

    private final String secret = "secret";
    private final Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());

    final long accessTokenMinBeforeExp = 30;
    final long refreshTokenMinBeforeExp = 60 * 24 * 14;

    /**
     * Creates an access and refresh token with the data provided
     * The access token expires after 30m
     * @param issuer path at which the token was created
     * @param role role of the client
     * @param id id of the client
     * @return A map with the access and refresh token
     */
    public Map<String, String> createTokens(String id, String issuer, Role role) {
        return createTokens(id, issuer, role, accessTokenMinBeforeExp);
    }

    /**
     * Creates an access and refresh token with the data provided
     * Added to create a token with custom expDate for test purposes
     * @param issuer path at which the token was created
     * @param role role of the client
     * @param id id of the client
     * @param minBeforeExp time before the token expires
     * @return A map with the access and refresh token
     */
    public Map<String, String> createTokens(String id, String issuer, Role role, long minBeforeExp) {
        if(id == null) {
            log.error("id is null");
        }
        String accessToken = JWT.create()
                .withSubject(id)
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 1000 * minBeforeExp))
                .withIssuer(issuer)
                .withClaim(Role.getClaimName(), role.getRole())
                .sign(algorithm);


        String refreshToken = JWT.create()
                .withSubject(id)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenMinBeforeExp * 60 * 1000))
                .withIssuer(issuer)
                .withClaim(Role.getClaimName(), role.getRole())
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        return tokens;
    }

    /**
     * Extract the token from a Bearer header and decrypt it, raise an error if the token is invalid
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
