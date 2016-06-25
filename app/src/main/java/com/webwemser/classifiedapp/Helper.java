package com.webwemser.classifiedapp;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMWriter;
import org.spongycastle.openssl.jcajce.JcaPEMWriter;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemWriter;


import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Christian on 03.06.2016.
 */
public class Helper {
    public static final String URL = "https://webengserver.herokuapp.com/";
    public static String getString(byte[] bytes) {
        return new String(bytes,StandardCharsets.UTF_8);
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
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    /**
     * Adapted from http://stackoverflow.com/questions/12116092/android-random-string-generator
     * @param sizeOfRandomString
     * @return
     */
    public static String getRandomString(final int sizeOfRandomString)
    {
        final SecureRandom random=new SecureRandom();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
    public static Uri.Builder getUriBuilder() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("webengserver.herokuapp.com");
        return builder;
    }
    public static String getPEMStringFromKey(Key key) {
        StringWriter pemStrWriter = new StringWriter();

      // PemWriter pemWriter = new PemWriter(pemStrWriter);
        JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(pemStrWriter);
        try {

            //pemWriter.writeObject(keyPair);
           // pemWriter.write(new PemObject("PUBLIC KEY",key.getEncoded()));
jcaPEMWriter.writeObject(key);
            jcaPEMWriter.close();
          //  pemWriter.flush();
           // pemWriter.close();

        } catch (IOException e) {
            Log.i("Caught exception:" , e.getMessage());


            return "";
        }

        return pemStrWriter.toString();
    }


}
