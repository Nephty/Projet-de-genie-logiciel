package com.example.demo.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

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
    public static Optional<Role> getRoleFromString(String roleStr) {
        for (Role role : Role.values()) {
            if(role.getRole().equals(roleStr)) return Optional.of(role);
        }
        return Optional.empty();
    }
}
