package com.example.demo.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.exception.throwables.AuthenticationException;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.example.demo.service.BankService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RequestMapping(path = "/api/token/refresh")
@RestController
public class TokenController {

    private final UserService userService;

    private final BankService bankService;
    /**
     * @param request Http request
     * @param response Http response
     * Send a new access and request token in the response body
     */
    @GetMapping
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        TokenHandler jwtHandler = new TokenHandler();
        DecodedJWT decodedJWT = jwtHandler.extractToken(authorizationHeader);

        String role = decodedJWT.getClaim(Role.getClaimName()).asString();
        String id = decodedJWT.getSubject();

        Map<String, String> tokens = jwtHandler.createTokens(
                id,
                request.getRequestURL().toString(),
                Role.getRoleFromString(role).orElseThrow(() -> new AuthenticationException("incorrect role: " + role))
        );

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
