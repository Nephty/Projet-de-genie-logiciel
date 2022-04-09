package com.example.demo.filter;

import com.example.demo.exception.throwables.MissingParamException;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j @RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /**
     * This method is called when a client tries to log in it reads the username, pwd and role
     * and pass them to the chain to check in the DB
     */
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        log.info("username: {}, pwd: {}, role {}", username, password, role);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username + "/" + role, password
        );
        String presentedPassword = authenticationToken.getCredentials().toString();
        log.info(presentedPassword);
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * This method is called when the user is successfully authenticated
     * It creates an access and refresh token and puts them in the body of the response
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        TokenHandler jwtHandler = new TokenHandler();
        User user = (User)authResult.getPrincipal();
        Role role = Role.getRoleFromString(request.getParameter("role"))
                .orElseThrow(()-> new MissingParamException(request.getParameter("role")));

        Map<String, String> tokens = jwtHandler.createTokens(
                user.getUsername(),
                request.getRequestURL().toString(),
                role
        );
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    /**
     * Creates a custom authentication error response
     */
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error:", failed.toString());
        log.error("unsuccessful auth: " + failed);
        response.setStatus(403);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
