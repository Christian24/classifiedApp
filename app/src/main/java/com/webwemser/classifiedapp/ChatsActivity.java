package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.webwemser.classifiedapp.requests.GetLastMessageRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatsActivity extends AppCompatActivity {

    public static final String USER = "USERNAME", CONTACT = "CONTACT", KEY_POSITION = "POSITION", PUBKEY = "PUBKEY";
    private EditText username;
    private ListView list;
    private MyListAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private GetLastMessageRequest lastMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        username = (EditText)findViewById(R.id.username);
        this.setTitle("Chats");
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        createSwipeLayout();
        lastMessage = new GetLastMessageRequest();
        try {
            lastMessage.start(getApplicationContext(), new SwipeRefreshLayout(getApplicationContext()));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void startChat(View view) {
        if(username.getText().toString().length()>0 && !(username.getText().toString().equals(Singleton.getSingleton().getLogin()))){
            login(username.getText().toString());
        }
        else{
            Toast.makeText(getApplicationContext(), "Bitte richtigen Benutzernamen eintragen", Toast.LENGTH_SHORT).show();
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
        Log.i("URL", Helper.URL + userName + "/pubkey");
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
                Log.i("Statuscode", response.statusCode+"");
                Response<JSONObject> json = super.parseNetworkResponse(response);
                try {
                    if (mStatusCode==200 || mStatusCode==304){
                        startChatActivity(json.result.getString("pubkey_user"));
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
                Log.i("PUBKEY", pubkey);
                startActivity(intent);
            }
        });
    }

    private void createSwipeLayout(){
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    lastMessage.start(getApplicationContext(), swipeContainer);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }
}
