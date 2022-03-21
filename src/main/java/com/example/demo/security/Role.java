package com.example.demo.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Different roles that a client can have
 */
@RequiredArgsConstructor
@Getter
public enum Role {
    USER("ROLE_USER"),
    BANK("ROLE_BANK");

    private final String role;
    public static String getClaimName() {
        return "role";
    }
}
