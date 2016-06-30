package com.webwemser.classifiedapp;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.jcajce.JcaPEMWriter;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Christian on 03.06.2016.
 */
public class Helper {
    public static final String URL = "https://webengserver.herokuapp.com/";


    public static SecretKeySpec buildKey(byte[] password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        Provider provider = new BouncyCastleProvider();
        MessageDigest digester = MessageDigest.getInstance("SHA-256", provider);
        digester.update(password);
        byte[] key = digester.digest();
        SecretKeySpec spec = new SecretKeySpec(key, "AES");
        return spec;
    }

    public static byte[] getBytes(String string) {
        return string.getBytes();
    }

    public static String base64Encoding(byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    public static byte[] base64Decoding(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }


    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";

    /**
     * Adapted from http://stackoverflow.com/questions/12116092/android-random-string-generator
     *
     * @param sizeOfRandomString
     * @return
     */
    public static String getRandomString(final int sizeOfRandomString) {
        final SecureRandom random = new SecureRandom();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static byte[] getRandomBytes(int number) {
        byte[] bytes = new byte[number];
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < number; i++) {
            byte[] temp = new byte[1];
            secureRandom.nextBytes(temp);
            while (temp[0] < 0) {
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

    public static PublicKey generatePublicKey(byte[] key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = null;

        keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey generatePrivateKey(byte[] key) throws InvalidKeySpecException, NoSuchAlgorithmException {


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        return keyFactory.generatePrivate(spec);
    }

    public static PublicKey getKeyFromPEM(String key) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {

        StringReader stringReader = new StringReader(key);

        PemReader pemReader = new PemReader(stringReader);


        PemObject obj = pemReader.readPemObject();
        pemReader.close();

        return generatePublicKey(obj.getContent());
    }

    public static String getPEMStringFromKey(Key key) {
        StringWriter pemStrWriter = new StringWriter();

        JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(pemStrWriter);
        try {
            jcaPEMWriter.writeObject(key);
            jcaPEMWriter.close();
        } catch (IOException e) {
            Log.i("Caught exception:", e.getMessage());
            return "";
        }

        return pemStrWriter.toString();
    }

    public static int getTimestamp() {
        long unixTime = System.currentTimeMillis() / 1000;
        return (int) unixTime;
    }

    public static byte[] generateSignature(PrivateKey key, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);

        signature.update(message.getBytes());
        return signature.sign();
    }

    public static boolean verifySignature(PublicKey key, byte[] data, byte[] signatureToVerify) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");

        signature.initVerify(key);
        signature.update(data);
        return signature.verify(signatureToVerify);

    }


}
