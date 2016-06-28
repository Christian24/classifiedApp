package com.webwemser.classifiedapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Singleton;
import org.json.JSONObject;
import org.spongycastle.pqc.math.ntru.polynomial.Constants;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
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

    public void send(View v){
        String m = message.getText().toString();
        if(m.length()>0){
            sendMessage(m);
        }
    }

    private void sendMessage(String message){
        try{
            SecureRandom random = new SecureRandom();

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            byte[] key_recipient = random.generateSeed(16);

            SecretKeySpec keySpec = new SecretKeySpec(key_recipient, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] message_enc = cipher.doFinal(Helper.getBytes(message));
            byte[] iv = cipher.getIV();
            Intent intent = getIntent();
            String pubkey_string = intent.getStringExtra(ChatsActivity.PUBKEY);
            Log.i("Pubkey_String", pubkey_string);
            Key pubkey = Helper.getKeyFromPEM(pubkey_string);
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, pubkey);
            byte[] key_recipient_enc = rsa.doFinal(key_recipient);
            String timestamp = Integer.toString( Helper.getTimestamp());
            byte[] digital_signature = Helper.generateSig_recipient(Singleton.getSingleton().getPrivate_key(),Singleton.getSingleton().getLogin(),message_enc,iv,key_recipient_enc);
            byte[] sig_service = Helper.generateSig_service(Singleton.getSingleton().getPrivate_key(),
                    Singleton.getSingleton().getLogin(),message_enc,
                    iv,key_recipient_enc,digital_signature,Helper.getBytes(timestamp),Helper.getBytes(username));
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("sender", Singleton.getSingleton().getLogin());
            params.put("content_enc", Helper.base64Encoding( Helper.getString(message_enc)));
            params.put("key_recipient_enc", Helper.base64Encoding(Helper.getString(key_recipient_enc)));
            params.put("iv", Helper.base64Encoding(Helper.getString(iv)) );
            params.put("sig_recipient", Helper.base64Encoding(Helper.getString(digital_signature)));
            params.put("timestamp", Integer.toString(Helper.getTimestamp()));
            params.put("sig_service", Helper.base64Encoding(Helper.getString(sig_service)));
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
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                // TODO Auto-generated method stub
                new AlertDialog.Builder(SendActivity.this)
                        .setTitle("Nachricht löschen")
                        .setMessage("Möchten sie die Nachricht wirklich löschen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(getResources().getDrawable(R.drawable.ic_delete_forever_black_24dp))
                        .show();
                Log.v("Long clicked","Position: " + pos);
                return true;
            }
        });
    }
}
