package com.example.demo.filter;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.exception.throwables.AuthenticationException;
import com.example.demo.security.Role;
import com.example.demo.security.TokenHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/user/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            TokenHandler jwtHandler = new TokenHandler();
            try {
                DecodedJWT decodedJWT = jwtHandler.extractToken(authorizationHeader);
                String username = decodedJWT.getSubject();
                String role = decodedJWT.getClaim(Role.getClaimName()).asString();
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (NestedServletException e) {
                writeErrorToResponse(response, HttpStatus.BAD_REQUEST.value(), e);
                log.error("Request format not respected: {}", e.getMessage());
                if(!(e.getCause() instanceof DataIntegrityViolationException)) {
                    log.error("Unknown cause:{}", e.getCause().toString());
                }
            } catch (Exception e) {
                log.error("Error type {}", e.getClass().toString());
                log.error("Error logging in {}", e.getMessage());
                jwtHandler.writeErrorToResponse(response, e, authorizationHeader);
            }
        }
    }
    private void writeErrorToResponse(HttpServletResponse response, int status, Exception e) throws IOException {
        response.setStatus(status);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error_msg", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }
}