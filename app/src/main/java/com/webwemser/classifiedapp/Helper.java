package com.webwemser.classifiedapp;

import android.util.Base64;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Christian on 03.06.2016.
 */
public class Helper {
    public static final String URL = "https://webengserver.herokuapp.com/";
    public static String getString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
   public static SecretKeySpec buildKey(byte[] password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Provider provider = new BouncyCastleProvider();
        MessageDigest digester = MessageDigest.getInstance("SHA-256", provider);
        digester.update(String.valueOf(password).getBytes("UTF-8"));
        byte[] key = digester.digest();
        SecretKeySpec spec = new SecretKeySpec(key, "AES");
        return spec;
    }
    public static byte[] getBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }
    public static String base64Encoding(String input) {
     return   getString(base64Encoding(getBytes(input)));
    }
    public static byte[] base64Encoding(byte[] input) {
       return Base64.encode(input,Base64.DEFAULT);
    }
    public static byte[] base64Decoding(byte[] input) {
       return Base64.decode(input,Base64.DEFAULT);
    }
    public static String base64Decoding(String input) {
    return getString(base64Decoding(getBytes(input)));
    }


}
