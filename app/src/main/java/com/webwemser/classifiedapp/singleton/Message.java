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
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Christian on 27.06.2016.
 */
public class Message extends Application {
private static Message instance;
    public HashMap<String,ArrayList<HashMap<String,String>>> conversations;
    private Message() {
        conversations = new HashMap<String,ArrayList<HashMap<String,String>>>();
    }
    public static Message getInstance() {
    if(instance == null)
    instance = new Message();
        return instance;
    }
    public void addMessage(String message, String recipient) {
        if(!conversations.containsKey(recipient)) {
            conversations.put(recipient, new ArrayList<HashMap<String, String>>());
        }
        ArrayList<HashMap<String,String>> conversation = conversations.get(recipient);
        HashMap<String,String> newMessage = new HashMap<>();
        newMessage.put(recipient,message);
        conversation.add(newMessage);

    }

    public void addMessage(Context context,JSONObject json) throws JSONException {
        int timestamp = json.getInt("timestamp");
        final String sender = json.getString("sender");
        final String content_string = json.getString("content_enc");
        final byte[] content_enc = Helper.base64Decoding(json.getString("content_enc"));

        final byte[] iv = Helper.base64Decoding(json.getString("iv"));
        final String iv_string = json.getString("iv");
        final byte[] key_recipient_enc = Helper.base64Decoding(json.getString("key_recipient_enc"));
        final String key_recipient_enc_string = json.getString("key_recipient_enc");
        final byte[] sig_recipient = Helper.base64Decoding(json.getString("sig_recipient"));
        byte[] sig_service = Helper.base64Decoding(json.getString("sig_service"));
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
                String pubkey_user;
                Log.i("Statuscode", response.statusCode+"");
                Response<JSONObject> json = super.parseNetworkResponse(response);
                try {
                    pubkey_user = json.result.getString("pubkey_user");

                    if (mStatusCode==200){

                     PublicKey publicKey   = Helper.getKeyFromPEM(pubkey_user);
                    String sig_recipient_data = new String(sender+content_string+iv_string+key_recipient_enc_string);
                    if( Helper.verifySignature(publicKey,sig_recipient_data.getBytes(),sig_recipient)) {
                        //Match
                        RSACipher rsaCipher = RSACipher.getInstance();
                       byte[] key_recipient = rsaCipher.decrypt(publicKey,key_recipient_enc);
                        AESCBC aescbc = AESCBC.getInstance();
                        byte[] message = aescbc.decrypt(key_recipient,iv,content_enc);
                        addMessage(new String(message),sender);
                    }
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
