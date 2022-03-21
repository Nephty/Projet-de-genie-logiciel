package com.example.demo.filter;

import com.example.demo.exception.throwables.MissingParamException;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        Map<String, String> tokens;
        Role role;
        User user = (User)authResult.getPrincipal();


        //ugly but necessary
        final String[] id = {null};
        user.getAuthorities().forEach(auth -> {
            if(auth.getAuthority().startsWith("id ")) {
                id[0] = auth.getAuthority().substring(3);
            }
        });
        if(id[0] == null) {
            log.error("id is null");
        }

        switch (request.getParameter("role")) {
            case "ROLE_USER":
                role = Role.USER;
                break;
            case "ROLE_BANK":
                role = Role.BANK;
                break;
            default:
                throw new MissingParamException(request.getParameter("role"));
        }
        tokens = jwtHandler.createTokens(
                user.getUsername(),
                request.getRequestURL().toString(),
                role,
                id[0]
        );
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error:", failed.toString());
        log.error("unsuccessful auth: " + failed);
        response.setStatus(401);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
