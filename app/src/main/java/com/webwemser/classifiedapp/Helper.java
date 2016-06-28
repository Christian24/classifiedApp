package com.webwemser.classifiedapp;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.PEMWriter;
import org.spongycastle.openssl.jcajce.JcaPEMWriter;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;
import org.spongycastle.util.io.pem.PemWriter;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
        digester.update(password);
        byte[] key = digester.digest();
        SecretKeySpec spec = new SecretKeySpec(key, "AES");
        return spec;
    }

    public static byte[] getBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String base64Encoding(String input) {
        return getString(base64Encoding(getBytes(input)));
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
    public static byte[] getRandomBytes(int number) {
        byte[] bytes = new byte[number];
        SecureRandom secureRandom = new SecureRandom();
        for(int i= 0;i< number;i++) {
        byte[] temp = new byte[1];
            secureRandom.nextBytes(temp);
            while(temp[0] < 0) {
                secureRandom.nextBytes(temp);
            }
            bytes[i] = temp[0];
        }
        return bytes;
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
    public static Key getKeyFromPEM(String key) {
        StringWriter pemStrWriter = new StringWriter();
        StringReader stringReader = new StringReader(key);
        // PemWriter pemWriter = new PemWriter(pemStrWriter);
        PemReader pemReader = new PemReader(stringReader);

        try {
            //pemWriter.writeObject(keyPair);
            // pemWriter.write(new PemObject("PUBLIC KEY",key.getEncoded()));
            PemObject obj = pemReader.readPemObject();
            pemReader.close();
            Log.i("PEM", Helper.getString(obj.getContent()));
            return  new SecretKeySpec(obj.getContent(), 0, obj.getContent().length, "RSA");
            //  pemWriter.flush();
            // pemWriter.close();

        } catch (IOException e) {
            Log.i("Caught exception:" , e.getMessage());
            return null;
        }
    }
    public static RSAPublicKey generatePublicKey(Key key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = null;

        keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncoded());
        return  (RSAPublicKey) keyFactory.generatePublic(spec);
    }

    public static int getTimestamp() {
        long unixTime = System.currentTimeMillis() / 1000L;
        return (int)unixTime;
    }

    public static byte[] generateSig_recipient(Key key, String identity,byte[] cipher,byte[] iv, byte[] key_recipient_enc) throws NoSuchAlgorithmException {
        return generateSig_recipient(key.getEncoded(),identity,cipher,iv,key_recipient_enc);
    }

    public static byte[] generateSig_recipient(byte[] key, String identity,byte[] cipher,byte[] iv, byte[] key_recipient_enc) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(key);
        digest.update(Helper.getBytes(identity));
        digest.update(cipher);
        digest.update(iv);
       return digest.digest(key_recipient_enc);
    }

    public static byte[] generateSig_service(byte[] key, String identity,byte[] cipher,byte[] iv, byte[] key_recipient_enc,
                                             byte[] sig_recipient, byte[] timestamp, byte[] recipient) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(key);
        digest.update(Helper.getBytes(identity));
        digest.update(cipher);
        digest.update(iv);
        digest.update(key_recipient_enc);
        digest.update(sig_recipient);
        digest.update(timestamp);
       return digest.digest(recipient);
    }
}
