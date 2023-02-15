package com.example.demo.other;

import com.example.demo.security.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Contains the data inside a token relevant to id the client
 * inserted as an httpRequest attribute in every authenticated request
 */
@Getter
@RequiredArgsConstructor
@ToString
public class Sender {

    private final String id;

    private final Role role;

    private final static String attributeName = "SENDER";

    public static String getAttributeName() {
        return attributeName;
    }

}