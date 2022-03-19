package com.example.demo.other;

import com.example.demo.exception.throwables.AuthenticationException;
import com.example.demo.security.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j @Getter @RequiredArgsConstructor @ToString
public class Sender {

    private final String id;

    private final Role role;

    private final static String attributeName = "SENDER";

    public static String getAttributeName() {
        return attributeName;
    }
}
