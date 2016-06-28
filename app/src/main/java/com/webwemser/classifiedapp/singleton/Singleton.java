package com.webwemser.classifiedapp.singleton;

import android.app.Application;

import java.security.PrivateKey;

/**
 * Created by Christian on 23.06.2016.
 */
public class Singleton extends Application {
private static Singleton singleton;
    protected byte[] pubkey;
    protected byte[] masterkey;
    protected byte[] salt_masterkey;
    protected String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getSalt_masterkey() {
        return salt_masterkey;
    }

    public void setSalt_masterkey(byte[] salt_masterkey) {
        this.salt_masterkey = salt_masterkey;
    }

    protected byte[] private_key_enc;
    protected PrivateKey private_key;

    public byte[] getPubkey() {
        return pubkey;
    }

    public void setPubkey(byte[] pubkey) {
        this.pubkey = pubkey;
    }

    public byte[] getMasterkey() {
        return masterkey;
    }

    public void setMasterkey(byte[] masterkey) {
        this.masterkey = masterkey;
    }

    public byte[] getPrivate_key_enc() {
        return private_key_enc;
    }

    public void setPrivate_key_enc(byte[] private_key_enc) {
        this.private_key_enc = private_key_enc;
    }

    public PrivateKey getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(PrivateKey private_key) {
        this.private_key = private_key;
    }

    private Singleton() {

    }
    public static Singleton getSingleton() {
        if(singleton == null)
            singleton = new Singleton();
        return singleton;
    }

}
