package com.example.demo.other;

import java.util.Random;

public class Generator {

    private static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                +"lmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
    //ugly find a better solution
    public static Integer nextTransactionId() {
        String numbers = "0123456789";
        String numbersNonNull = "123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(9);
        sb.append(numbers.charAt(rnd.nextInt(numbersNonNull.length())));
        for (int i = 0; i < 8; i++)
            sb.append(numbers.charAt(rnd.nextInt(numbers.length())));
        return Integer.parseInt(sb.toString());
    }

}
