package com.webwemser.classifiedapp.singleton;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.Helper;
import com.webwemser.classifiedapp.requests.RequestSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.PublicKey;

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
    public void addMessage(Context context,JSONObject json) throws JSONException {
        int timestamp = json.getInt("timestamp");
        String sender = json.getString("sender");
        String cipher = Helper.base64Encoding(json.getString("content_enc"));
        String iv = json.getString("iv");
        String key_recipient_enc = Helper.base64Decoding(json.getString("key_recipient_enc"));
        String sig_recipient = Helper.base64Decoding(json.getString("sig_recipient"));
        String sig_service = Helper.base64Decoding(json.getString("sig_service"));
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Helper.getUriBuilder().appendPath(sender).appendPath("pubkey").toString(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Log Response ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                //get status code here
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                Log.i("Log VolleyError", statusCode);
                if (error.networkResponse.data != null) {
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        Log.i("Log VolleyError", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                String pubkey_user = "";
                Log.i("Statuscode", response.statusCode+"");
                Response<JSONObject> json = super.parseNetworkResponse(response);
                try {
                    pubkey_user = json.result.getString("pubkey_user");
                    Log.i("Pubkey_User", pubkey_user);
                    if (mStatusCode==200){
                      Key key= Helper.getKeyFromPEM( pubkey_user);

                    }
                }
                catch (Exception e){

                }
                return json;
            }
        };
        RequestSingleton.getInstance(context).add(request);


    }
}
