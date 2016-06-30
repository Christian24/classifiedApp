package com.webwemser.classifiedapp.requests;

import android.net.Uri;

import com.webwemser.classifiedapp.Helper;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Sergei on 30.06.2016.
 */
public class Delete {

    public static void DeleteMessage(String username) {
        String timestamp = Integer.toString(Helper.getTimestamp());
        String digitalSignature = "mock";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("login", username);
        params.put("timestamp", timestamp);
        params.put("digitale_signatur", digitalSignature);
        JSONObject json = new JSONObject(params);
        Uri url = Helper.getUriBuilder().appendPath(username).appendPath("message").build();
        // JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,url.toString(),json,new Response.Listener<JSONObject>() {
    }

}
