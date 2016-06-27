package com.webwemser.classifiedapp.requests;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.Helper;
import com.webwemser.classifiedapp.singleton.Message;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.crypto.digests.SHA256Digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by Christian on 27.06.2016.
 */
public class GetLastMessageRequest {

    public void start(final Context context) throws NoSuchAlgorithmException {
        String login = Singleton.getSingleton().getLogin();
        int timestamp = Helper.getTimestamp();
       MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(Helper.getBytes(login));
        byte[] digitale_signatur = digest.digest(Helper.getBytes(new Integer(timestamp).toString()));
        HashMap<String,String> map = new HashMap<>();
        map.put("login",login);
        map.put("timestamp",Integer.toString(timestamp));
        map.put("digitale_signatur",Helper.getString(digitale_signatur));

        JSONObject json = new JSONObject(map);
        Uri url = Helper.getUriBuilder().appendPath(login).appendPath("message").build();
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
        })
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Log.i("Log ParseResponse", response.statusCode+"");
                Response<JSONObject> json = super.parseNetworkResponse(response);
                int mStatusCode = response.statusCode;
                if(mStatusCode==200){
                    try {
                        Message.getInstance().addMessage(context,json.result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return json;
            }
        };

        RequestSingleton.getInstance(context).add(request);


    }
}
