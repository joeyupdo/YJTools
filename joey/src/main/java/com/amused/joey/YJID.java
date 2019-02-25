package com.amused.joey;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class YJID {
    private YJID() { }

    public static String getMD5(String src, boolean upperCase) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(src.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x : digest) {
            if((x & 0xff) >> 4 == 0) {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return upperCase? sb.toString().toUpperCase(): sb.toString();
    }

    public static String getRandomString(int length, boolean upperCase) {
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            str.append((char) (Math.random() * 26 + (random.nextBoolean() ? 65 : 97)));
        }
        return upperCase? str.toString().toUpperCase(): str.toString();
    }
}
