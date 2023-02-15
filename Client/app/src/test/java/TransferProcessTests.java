import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static front.controllers.TransferSceneController.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Arnaud MOREAU
 */
public class TransferProcessTests {
    final String a0 = "";
    final String a1 = null;
    final String a2 = "0";
    final String a3 = "0.000";
    final String a4 = "0000.00";
    final String a5 = "10.10.10";
    final String a6 = "10";
    final String a7 = "10.10";
    final String a8 = "01";
    final String a9 = "1111111111111";
    final String a10 = "123123123.12";
    final String r0 = "";
    final String r1 = null;
    final String r2 = "1";
    final String r3 = "a0";
    final String r4 = "y";
    final String r5 = "=";
    final String r6 = "@";
    final String r7 = "hello";
    final String r8 = "ABC";
    final String i0 = "";
    final String i1 = null;
    final String i2 = "0";
    final String i3 = "abc0123412341234";
    final String i4 = "aB121234123412341";
    final String i5 = "1234123412341234";
    final String i6 = "nA12123412341234";
    final String m0 = "";
    final String m1 = null;
    final String m2 = "0";
    final String m3 = "h";
    final String m4 = "\t";
    final String m5 = "12345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456789";
    final String d0 = "";
    final String d1 = null;
    final String d2 = "a";
    final String d3 = "ab.ab.abcd";
    final String d4 = "00-00-0000";
    final String d5 = "12.12.2019";
    final String d6 = "32.10.2021";
    final String d7 = "00.02.2023";
    final String d8 = "1.1.2023";
    final String d9 = "01.01.2022";

    @Test
    @DisplayName("isValidAmount Test")
    public void isValidAmountTest() {
        assertFalse(isValidAmount(a0));
        assertFalse(isValidAmount(a1));
        assertFalse(isValidAmount(a2));
        assertFalse(isValidAmount(a3));
        assertFalse(isValidAmount(a4));
        assertFalse(isValidAmount(a5));
        assertTrue(isValidAmount(a6));
        assertTrue(isValidAmount(a7));
        assertTrue(isValidAmount(a8));
        assertFalse(isValidAmount(a9));
        assertTrue(isValidAmount(a10));
    }

    @Test
    @DisplayName("isValidRecipient Test")
    public void isValidRecipientTest() {
        assertTrue(isValidRecipient(r0));
        assertTrue(isValidRecipient(r1));
        assertFalse(isValidRecipient(r2));
        assertFalse(isValidRecipient(r3));
        assertTrue(isValidRecipient(r4));
        assertFalse(isValidRecipient(r5));
        assertFalse(isValidRecipient(r6));
        assertTrue(isValidRecipient(r7));
        assertTrue(isValidRecipient(r8));
    }

    @Test
    @DisplayName("isValidIBAN Test")
    public void isValidIBANTest() {
        assertFalse(isValidIBAN(i0));
        assertFalse(isValidIBAN(i1));
        assertFalse(isValidIBAN(i2));
        assertFalse(isValidIBAN(i3));
        assertFalse(isValidIBAN(i4));
        assertFalse(isValidIBAN(i5));
        assertTrue(isValidIBAN(i6));
    }

    @Test
    @DisplayName("isValidMessage Test")
    public void isValidMessageTest() {
        assertTrue(isValidMessage(m0));
        assertTrue(isValidMessage(m1));
        assertTrue(isValidMessage(m2));
        assertTrue(isValidMessage(m3));
        assertFalse(isValidMessage(m4));
        assertFalse(isValidMessage(m5));
    }

    @Test
    @DisplayName("isValidDate Test")
    public void isValidDateTest() {
        assertTrue(isValidDate(d0));
        assertTrue(isValidDate(d1));
        assertFalse(isValidDate(d2));
        assertFalse(isValidDate(d3));
        assertFalse(isValidDate(d4));
        assertFalse(isValidDate(d5));
        assertFalse(isValidDate(d6));
        assertFalse(isValidDate(d7));
        assertFalse(isValidDate(d8));
        assertTrue(isValidDate(d9));
    }
}
