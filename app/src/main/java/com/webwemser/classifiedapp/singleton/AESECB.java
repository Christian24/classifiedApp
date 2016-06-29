package com.webwemser.classifiedapp.singleton;

import android.app.Application;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Christian on 29.06.2016.
 */
public class AESECB extends Application {
    private static AESECB instance;
    private AESECB(){}
    public static AESECB getInstance() {
        if(instance == null)
            instance = new AESECB();
        return instance;
    }

    /**
     * Gets the cipher
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     */
    public Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/ECB/PKCS5Padding");
    }

    /**
     * Encrypts stuff
     * @param key
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encrypt(byte[] key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE,getKey(key));
       return cipher.doFinal(data);
    }

    /**
     * Builds a key
     * @param key
     * @return
     */
    public SecretKeySpec getKey(byte[] key) {
        return new SecretKeySpec(key,"AES");
    }

    /**
     * Decrypts stuff
     * @param masterkey
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] decrypt(byte[] masterkey, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE,getKey(masterkey));
        return cipher.doFinal(data);
    }
}
