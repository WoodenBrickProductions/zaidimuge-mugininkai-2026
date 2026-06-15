package com.example.universityjava;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHashing {
    public static String hashPassword(String password) throws NoSuchAlgorithmException {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.reset();
            md.update(password.getBytes());
            byte[] mdArray = md.digest();
            StringBuilder sb = new StringBuilder(mdArray.length * 2);
            for (byte b : mdArray) {
                int v = b & 0xff;
                if (v < 16)
                    sb.append('0');
                sb.append(Integer.toHexString(v));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new NoSuchAlgorithmException("No algorithm for SHA-512");
        }
    }
}