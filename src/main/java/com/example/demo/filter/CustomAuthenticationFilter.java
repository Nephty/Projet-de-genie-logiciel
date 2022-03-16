package com.example.demo.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.model.Bank;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j @RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        request.setAttribute("role", role);
        log.info("username: {}, pwd: {}, role {}", username, password, request.getAttribute("role"));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username + "/" + role, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        log.info("[SUCCESS]");
        TokenHandler jwtHandler = new TokenHandler();
        Map<String, String> tokens;
        switch (request.getParameter("role")) {
            case "ROLE_USER":
                User user = (User)authResult.getPrincipal();
                tokens = jwtHandler.createTokens(
                        user.getUsername(),
                        request.getRequestURL().toString(),
                        Role.USER
                );
                break;
            case "ROLE_BANK":
                User bank = (User)authResult.getPrincipal();
                tokens = jwtHandler.createTokens(
                        bank.getUsername(),
                        request.getRequestURL().toString(),
                        Role.BANK
                );
                break;
            default:
                throw new MissingParamException(request.getParameter("role"));
        }
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
