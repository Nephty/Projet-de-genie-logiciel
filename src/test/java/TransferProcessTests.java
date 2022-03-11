import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static front.controllers.TransferSceneController.*;
import static org.junit.jupiter.api.Assertions.*;

public class TransferProcessTests {
    String a0 = "", a1 = null, a2 = "0", a3 = "0.000", a4 = "0000.00", a5 = "10.10.10", a6 = "10", a7 = "10.10", a8 = "01", a9 = "1111111111111", a10 = "123123123.12";
    String r0 = "", r1 = null, r2 = "1", r3 = "a0", r4 = "y", r5 = "=", r6 = "@", r7 = "hello", r8 = "ABC";
    String i0 = "", i1 = null, i2 = "0", i3 = "abc0123412341234", i4 = "aB121234123412341", i5 = "1234123412341234", i6 = "nA12123412341234";
    String m0 = "", m1 = null, m2 = "0", m3 = "h", m4 = "\t", m5 = "12345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456781234567812345678123456789";
    String d0 = "", d1 = null, d2 = "a", d3 = "ab.ab.abcd", d4 = "00-00-0000", d5 = "12.12.2019", d6 = "32.10.2021", d7 = "00.02.2023", d8 = "1.1.2023", d9 = "01.01.2022";

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
