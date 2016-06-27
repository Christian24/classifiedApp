package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ChatsActivity extends AppCompatActivity {

    public static final String USER = "USERNAME", CONTACT = "CONTACT", KEY_POSITION = "POSITION", PUBKEY = "PUBKEY";
    private EditText username;
    private ListView list;
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_actitvity);
        username = (EditText)findViewById(R.id.username);
        this.setTitle("Chats");
    }

    public void startChat(View view) {
        if(username.getText().toString().length()>0){
            login(username.getText().toString());
        }
        else{
            Toast.makeText(getApplicationContext(), "Bitte Benutzernamen eintragen", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChats(ArrayList<String> names){
        ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < names.size(); i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(CONTACT, names.get(i));
            chatList.add(map);
        }
        list = (ListView)findViewById(R.id.list);
        // Getting adapter by passing xml data ArrayList
        adapter = new MyListAdapter(this, chatList);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatsActivity.this, SendActivity.class);
                intent.putExtra(KEY_POSITION, position);
                startActivity(intent);
        }
    });
    }

    protected void login(final String userName) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Helper.URL + userName + "/pubkey", null, new Response.Listener<JSONObject>() {
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
                        startChatActivity(pubkey_user);
                    }
                }
                catch (Exception e){

                }
                return json;
            }
        };
        RequestSingleton.getInstance(getApplicationContext()).add(request);
    }

    public void startChatActivity(final String pubkey){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ChatsActivity.this, SendActivity.class);
                intent.putExtra(USER, username.getText().toString());
                intent.putExtra(PUBKEY, pubkey);
                startActivity(intent);
            }
        });
    }
}
