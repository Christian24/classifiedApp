package com.webwemser.classifiedapp.singleton;

import android.app.Application;

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
    public void addMessage(JSONObject json) {

    }
}
