package com.webwemser.classifiedapp.requests;

import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.Helper;
import com.webwemser.classifiedapp.MessageObject;
import com.webwemser.classifiedapp.singleton.Message;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;

/**
 * Created by Sergei on 30.06.2016.
 */
public class Delete {

    String username;

    public static void DeleteMessage(String username)  throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String timestamp = Integer.toString(Helper.getTimestamp());

        String login = Singleton.getSingleton().getLogin();
        Singleton singleton = Singleton.getSingleton();
        String signatur_String = new String(login + timestamp);
        byte[] digitale_signatur = new byte[0];

        digitale_signatur = Helper.generateSignature(singleton.getPrivate_key(),signatur_String);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("login", username);
        params.put("timestamp", timestamp);
        params.put("digitale_signatur", digitale_signatur.toString());

        JSONObject json = new JSONObject(params);
        Uri url = Helper.getUriBuilder().appendPath(username).appendPath("message").build();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url.toString(),json,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Log Response ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                //get status code here
                if(error.networkResponse!=null){
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    Log.i("Log VolleyError", statusCode);
                    if(error.networkResponse.data!=null) {
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                            Log.i("Log VolleyError", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}