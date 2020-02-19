package com.clevel.kudu.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptUtil {
    private static final Logger log = LoggerFactory.getLogger(EncryptUtil.class);
    private static final String EMPTY_STRING = "";
    private static final String tmpKey = "ewLEMUmZHvKtg6ZiUtw1C71gOxAbplJz4HCjqRYGLsI=";

    private EncryptUtil() {
    }

    public static byte[] getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[32];
            sr.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            log.error("",e);
            return new byte[1];
        }
    }

    private static byte[] getTmpKey() {
        return tmpKey.getBytes();
    }

    private static String getHmacSHA256SecurePassword(String passwordToHash, byte[] salt, byte[] pepper) {
        String generatedPassword = null;
        try {
            Mac md = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(salt,"HmacSHA256");
            md.init(secretKeySpec);
            byte[] password = passwordToHash.getBytes();
            byte[] input = new byte[password.length+salt.length+pepper.length];
            System.arraycopy(password,0,input,0,password.length);
            System.arraycopy(salt,0,input,password.length,salt.length);
            System.arraycopy(pepper,0,input,password.length,pepper.length);
            byte[] bytes = md.doFinal(input);

            generatedPassword =  Base64.getEncoder().encodeToString(bytes)+":"+ Base64.getEncoder().encodeToString(salt);
            log.trace("salt: {}", Base64.getEncoder().encodeToString(salt));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("",e);
        }
        return generatedPassword;
    }

    public static String getPasswordHash(String input) {
        if (input==null || input.isEmpty()) {
            return EMPTY_STRING;
        }
        byte[] salt = getSalt();
        return getHmacSHA256SecurePassword(input,salt, getTmpKey());
    }

    public static String getPasswordHash(String input, byte[] salt) {
        if (input==null || input.isEmpty()) {
            return EMPTY_STRING;
        }
        return getHmacSHA256SecurePassword(input,salt, getTmpKey());
    }

}
