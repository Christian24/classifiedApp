package com.webwemser.classifiedapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONObject;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.pqc.math.ntru.polynomial.Constants;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SendActivity extends AppCompatActivity {

    public static final String SENDER = "SENDER", MESSAGE = "MESSAGE";
    private EditText message;
    private MyChatAdapter adapter;
    private ListView list;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        message = (EditText)findViewById(R.id.message);
        Intent intent = getIntent();
        username = intent.getStringExtra(ChatsActivity.USER);
        this.setTitle(username);
        message.setHint("Message to " + username);
        showChats();
    }

    public void sendMessage(String message){
        try{

            Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");
            byte[] static_key = Helper.getRandomBytes(16);
            SecretKeySpec keySpec = new SecretKeySpec(static_key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(Constants.IV_VECTOR);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] results = cipher.doFinal(message.getBytes());

            String result = Base64.encodeToString(results, Base64.NO_WRAP|Base64.DEFAULT);




            HashMap<String,String> params = new HashMap<String,String>();
            params.put("sender", Singleton.getSingleton().getLogin());

            params.put("content_enc", "");

            params.put("key_recipient_enc", "");

            params.put("sig_recipient", "");

            params.put("timestamp", Integer.toString(Helper.getTimestamp()));

            params.put("sig_service", "");

            params.put("recipitent", username);



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
            })
            {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Log.i("Log ParseResponse", response.statusCode+"");
                    int mStatusCode = response.statusCode;
                    if(mStatusCode==201){


                    }
                    return super.parseNetworkResponse(response);
                }
            };
            RequestSingleton.getInstance(getApplicationContext()).add(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showChats(){
        ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();
        int x = 0;
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            if(x%2==0)map.put(SENDER, "Matthias");
            if(x%2!=0)map.put(SENDER, "Self");
            map.put(MESSAGE, "Hallo");
            chatList.add(map);
            x++;
        }
        list = (ListView)findViewById(R.id.list_messages);
        // Getting adapter by passing xml data ArrayList
        adapter = new MyChatAdapter(this, chatList);
        list.setAdapter(adapter);
    }
}
