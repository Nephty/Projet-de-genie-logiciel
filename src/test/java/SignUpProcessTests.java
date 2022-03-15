import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static front.controllers.SignUpSceneController.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignUpProcessTests {
    String s0 = "", s1 = "b", s2 = "B", s3 = "b0", s4 = "b@", s5 = "b.", s6 = "b!", s7 = "b<", s8 = ">b", s9 = " b";
    String e0 = "", e1 = "b", e2 = "B", e3 = "e0", e4 = "b@", e5 = "b.", e6 = "b!", e7 = "a.b@c.d", e8 = "a@c.d", e9 = "@c.d", e10 = "a.b", e11 = "a.b.c@c.d", e12 = "a.b.c.d@c", e13 = "a@c", e14 = "a@c@d", e15 = "a@c.d.e";
    String n0 = "", n1 = "b", n2 = "1.2", n3 = "1.2.3", n4 = "1.2.3-4.5", n5 = "00.00.00-000.00", n6 = "00.00.0-000.00", n7 = "00.00-000.00", n8 = "00.0.00-000.00";

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