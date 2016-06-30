package com.webwemser.classifiedapp.requests;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.HashMap;

/**
 * Created by Christian on 27.06.2016.
 */
public class GetLastMessageRequest {

    public void start(final Context context, final SwipeRefreshLayout swipe) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        String login = Singleton.getSingleton().getLogin();
        String timestamp = Integer.toString( Helper.getTimestamp());
        Singleton singleton = Singleton.getSingleton();

        String signatur_String = new String(login + timestamp);

        byte[] digitale_signatur = Helper.generateSignature(singleton.getPrivate_key(),signatur_String);
        String digitale_signaturString = Base64.encodeToString(digitale_signatur,Base64.DEFAULT);
        Signature sig = Signature.getInstance("SHA256withRSA");
                sig.initSign(singleton.getPrivate_key());

                 sig.update(signatur_String.getBytes());
        byte[] sig_service = sig.sign();

        Signature s1 = Signature.getInstance("SHA256withRSA");
        s1.initVerify(singleton.getPubkey());

        boolean ok = Helper.verifySignature(singleton.getPubkey(),signatur_String.getBytes(),sig_service);
        HashMap<String,String> map = new HashMap<>();
        map.put("login",login);
        map.put("timestamp",timestamp);
        map.put("digitale_signatur",digitale_signaturString);

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
                    Log.i("Error GetLastMessage", statusCode);
                    if(error.networkResponse.data!=null) {
                        try {
                            body = new String(error.networkResponse.data,"UTF-8");
                            Log.i("Error GetLastMessage", body);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                swipe.setRefreshing(false);
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
                swipe.setRefreshing(false);
                return json;
            }
        };
        RequestSingleton.getInstance(context).add(request);


    }
}
