package com.webwemser.classifiedapp;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;


import com.google.common.primitives.Bytes;

import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openssl.jcajce.JcaPEMWriter;
import org.spongycastle.util.io.pem.PemObject;
import org.spongycastle.util.io.pem.PemReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Christian on 03.06.2016.
 */
public class Helper {
    public static final String URL = "https://webengserver.herokuapp.com/";
    public static String getString(byte[] bytes) {
        return new String(bytes,StandardCharsets.ISO_8859_1);
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
        return string.getBytes(StandardCharsets.ISO_8859_1);
    }

    public static String base64Encoding(String input) {
        return getString(base64Encoding(getBytes(input)));
    }

    public static byte[] base64Encoding(byte[] input) {
       return Base64.encode(input,Base64.NO_PADDING | Base64.NO_WRAP);
    }

    public static byte[] base64Decoding(byte[] input) {
       return Base64.decode(input,Base64.NO_PADDING | Base64.NO_WRAP);
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
    public static RSAPrivateKey generatePrivateKey(Key key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = null;

        keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec spec = new X509EncodedKeySpec(key.getEncoded());
        return  (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }
    public static RSAPrivateKey generatePrivateKey(byte[] key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = null;

        keyFactory = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
        return  (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    public static int getTimestamp() {
        long unixTime = System.currentTimeMillis()/1000;
        return (int)unixTime;
    }

    public static byte[] generateSignature(PrivateKey key, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);

       signature.update(Helper.getBytes(message));
    return signature.sign();
    }
    public static boolean verifySignature(PublicKey key, byte[] signatureToVerify) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(key);
       return signature.verify(signatureToVerify);

    }



}
