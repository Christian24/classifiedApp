package com.webwemser.classifiedapp.singleton;

import android.app.Application;

import com.webwemser.classifiedapp.Helper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Christian on 27.06.2016.
 */
public class Message extends Application {
private static Message instance;
    private Message() {}
    public static Message getInstance() {
    if(instance == null)
    instance = new Message();
        return instance;
    }
    public void addMessage(JSONObject json) throws JSONException {
        int timestamp = json.getInt("timestamp");
        String sender = json.getString("sender");
        String cipher = Helper.base64Encoding(json.getString("content_enc"));
        String iv = json.getString("iv");
        String key_recipient_enc = Helper.base64Decoding(json.getString("key_recipient_enc"));
        String sig_recipient = Helper.base64Decoding(json.getString("sig_recipient"));
        String sig_service = Helper.base64Decoding(json.getString("sig_service"));


    }
}
