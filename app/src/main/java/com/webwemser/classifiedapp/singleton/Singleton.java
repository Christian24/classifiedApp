package com.webwemser.classifiedapp.singleton;

import android.app.Application;

/**
 * Created by Christian on 23.06.2016.
 */
public class Singleton extends Application {
private static Singleton singleton;
    protected String pubkey;
    protected String masterkey;
    protected String salt_masterkey;
    protected String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt_masterkey() {
        return salt_masterkey;
    }

    public void setSalt_masterkey(String salt_masterkey) {
        this.salt_masterkey = salt_masterkey;
    }

    protected String private_key_enc;
    protected String private_key;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getMasterkey() {
        return masterkey;
    }

    public void setMasterkey(String masterkey) {
        this.masterkey = masterkey;
    }

    public String getPrivate_key_enc() {
        return private_key_enc;
    }

    public void setPrivate_key_enc(String private_key_enc) {
        this.private_key_enc = private_key_enc;
    }

    public String getPrivate_key() {
        return private_key;
    }

    public void setPrivate_key(String private_key) {
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
