package com.example.demo.filter;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.other.Sender;
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
    /**
     *Checks if the token is valid or not and respond with an error in the latter case
     * It also writes the id and role of the client fo more complex authorization in the controllers
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if(noAuthorizationNecessary(request)) {
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
                //thanks to the weird new login the bank login with its id and the user with its username so the values
                //are inverted in a token from a bank(userId = bank login and username = swift)
                request.setAttribute(
                        Sender.getAttributeName(),
                        new Sender(
                                username,
                                role.equals(Role.USER.getRole()) ? Role.USER : Role.BANK
                        )
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            } catch (NestedServletException e) {
                writeErrorToResponse(response, HttpStatus.BAD_REQUEST.value(), e);
                log.warn("Request format not respected: {}", e.getMessage());
                if(!(e.getCause() instanceof DataIntegrityViolationException)) {
                    log.error("Unknown cause:{}", e.getCause().toString());
                }
            } catch (TokenExpiredException e) {
                writeErrorToResponse(response, HttpStatus.PRECONDITION_FAILED.value(), e);
                log.warn("Token expired");
            }
            catch (Exception e) {
                log.error("Error type {}", e.getClass().toString());
                log.error("Error logging in {}", e.getMessage());
                writeErrorToResponse(response, 401 ,e);
            }
        }
    }

    /**
     * @param response Http response
     * @param status Http response status
     * @param e Exception that was raised
     */
    private void writeErrorToResponse(HttpServletResponse response, int status, Exception e) throws IOException {
        response.setStatus(status);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error_msg", e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }

    /**
     * Determines whether a request needs to be authorized or not by looking at its path and method
     */
    private boolean noAuthorizationNecessary(HttpServletRequest request) {
        if(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            return true;
        }
        return request.getMethod().equals("POST") &&
                (request.getServletPath().equals("/api/user") || request.getServletPath().equals("/api/bank"));
    }
}
