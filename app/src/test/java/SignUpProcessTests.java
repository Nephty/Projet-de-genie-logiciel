import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static front.controllers.SignUpSceneController.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arnaud MOREAU
 */
public class SignUpProcessTests {
    final String s0 = "";
    final String s1 = "b";
    final String s2 = "B";
    final String s3 = "b0";
    final String s4 = "b@";
    final String s5 = "b.";
    final String s6 = "b!";
    final String s7 = "b<";
    final String s8 = ">b";
    final String s9 = " b";
    final String e0 = "";
    final String e1 = "b";
    final String e2 = "B";
    final String e3 = "e0";
    final String e4 = "b@";
    final String e5 = "b.";
    final String e6 = "b!";
    final String e7 = "a.b@c.d";
    final String e8 = "a@c.d";
    final String e9 = "@c.d";
    final String e10 = "a.b";
    final String e11 = "a.b.c@c.d";
    final String e12 = "a.b.c.d@c";
    final String e13 = "a@c";
    final String e14 = "a@c@d";
    final String e15 = "a@c.d.e";
    final String n0 = "";
    final String n1 = "b";
    final String n2 = "1.2";
    final String n3 = "1.2.3";
    final String n4 = "1.2.3-4.5";
    final String n5 = "00.00.00-000.00";
    final String n6 = "00.00.0-000.00";
    final String n7 = "00.00-000.00";
    final String n8 = "00.0.00-000.00";

    @Test
    @DisplayName("isValidLastName method Test")
    public void isValidLastNameTest() {
        assertFalse(isValidLastName(s0));
        assertTrue(isValidLastName(s1));
        assertTrue(isValidLastName(s2));
        assertFalse(isValidLastName(s3));
        assertFalse(isValidLastName(s4));
        assertFalse(isValidLastName(s5));
        assertFalse(isValidLastName(s6));
        assertFalse(isValidLastName(s7));
        assertFalse(isValidLastName(s8));
        assertFalse(isValidLastName(s9));
    }

    @Test
    @DisplayName("isValidFirstName method Test")
    public void isValidFirstNameTest() {
        assertFalse(isValidFirstName(s0));
        assertTrue(isValidFirstName(s1));
        assertTrue(isValidFirstName(s2));
        assertFalse(isValidFirstName(s3));
        assertFalse(isValidFirstName(s4));
        assertFalse(isValidFirstName(s5));
        assertFalse(isValidFirstName(s6));
        assertFalse(isValidFirstName(s7));
        assertFalse(isValidFirstName(s8));
        assertFalse(isValidFirstName(s9));
    }

    @Test
    @DisplayName("isValidEmail method Test")
    public void isValidEmailTest() {
        assertFalse(isValidEmail(e0));
        assertFalse(isValidEmail(e1));
        assertFalse(isValidEmail(e2));
        assertFalse(isValidEmail(e3));
        assertFalse(isValidEmail(e4));
        assertFalse(isValidEmail(e5));
        assertFalse(isValidEmail(e6));
        assertTrue(isValidEmail(e7));
        assertTrue(isValidEmail(e8));
        assertFalse(isValidEmail(e9));
        assertFalse(isValidEmail(e10));
        assertTrue(isValidEmail(e11));
        assertFalse(isValidEmail(e12));
        assertFalse(isValidEmail(e13));
        assertFalse(isValidEmail(e14));
        assertTrue(isValidEmail(e15));
    }

    @Test
    @DisplayName("isValidNRN method test")
    public void isValidNRNTest() {
        assertFalse(isValidNRN(n0));
        assertFalse(isValidNRN(n1));
        assertFalse(isValidNRN(n2));
        assertFalse(isValidNRN(n3));
        assertFalse(isValidNRN(n4));
        assertTrue(isValidNRN(n5));
        assertFalse(isValidNRN(n6));
        assertFalse(isValidNRN(n7));
        assertFalse(isValidNRN(n8));
    }
}