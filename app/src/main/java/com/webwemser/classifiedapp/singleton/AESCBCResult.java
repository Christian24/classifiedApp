package com.webwemser.classifiedapp.singleton;

/**
 * Created by Christian on 29.06.2016.
 */
public class AESCBCResult {
    protected byte[] iv;
    protected byte[] data;

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
