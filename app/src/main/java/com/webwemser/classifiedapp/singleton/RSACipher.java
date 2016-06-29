package com.webwemser.classifiedapp.singleton;

import android.app.Application;

import com.webwemser.classifiedapp.Helper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Christian on 29.06.2016.
 */
public class RSACipher extends Application {
    private static RSACipher instance;
    private RSACipher() {}

    /**
     * Get instance
     * @return
     */
    public static RSACipher getInstance() {
        if(instance == null)
            instance = new RSACipher();

        return instance;
    }
    /**
     * gets the Cipher
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
       return Cipher.getInstance("RSA");
    }

    /**
     * Generates the key
     * @param pem
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public RSAPublicKey generateKey(String pem) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return Helper.generatePublicKey( Helper.getKeyFromPEM(pem));
    }

    /**
     * Encrypt data
     * @param publicKey
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeySpecException
     */
    public byte[] encrypt(String publicKey, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
    return encrypt(generateKey(publicKey),data);
    }
    /**
     * Encrypt
     * @param publicKey
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * Decrypt
     * @param pem
     * @param data
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     */
    public byte[] decrypt(String pem,byte[] data) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
        return decrypt(generateKey(pem),data);
    }

    /**
     * Decrypt
     * @param publicKey
     * @param data
     * @return
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] decrypt(RSAPublicKey publicKey, byte[] data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

}
