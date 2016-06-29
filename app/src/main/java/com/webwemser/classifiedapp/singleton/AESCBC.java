package com.webwemser.classifiedapp.singleton;

import android.app.Application;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Christian on 29.06.2016.
 */
public class AESCBC extends Application {
    private static AESCBC instance;
    private AESCBC() {}
    public static AESCBC getInstance() {
        if(instance == null)
            instance = new AESCBC();
        return instance;
    }

    /**
     * get a new Cipher instance
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    /**
     * Encrypts data
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public AESCBCResult encrypt(byte[] key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return encrypt(generateKey(key),data);
    }
    /**
     * Encrypts the given data with given key
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public AESCBCResult encrypt(Key key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] enc= cipher.doFinal(data);
        AESCBCResult result = new AESCBCResult();
        result.setIv(cipher.getIV());
        result.setData(enc);
        return result;
    }

    /**
     * Generates a key
     * @param key
     * @return
     */
    public SecretKeySpec generateKey(byte[] key) {
        return new SecretKeySpec(key,"AES");
    }

    /**
     * Decrypts data
     * @param key
     * @param iv
     * @param data
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decrypt(byte[] key, byte[] iv, byte[] data ) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
    return decrypt(generateKey(key),iv,data);
    }

    /**
     * Decrypt using key
     * @param key
     * @param iv
     * @param data
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decrypt(Key key, byte[] iv, byte[] data ) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE,key, new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }
}
