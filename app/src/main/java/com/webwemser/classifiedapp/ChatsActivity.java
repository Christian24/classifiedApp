package com.webwemser.classifiedapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.GetLastMessageRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Message;
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
    private ProgressBar progressBar;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        progressBar = (ProgressBar)findViewById(R.id.progess);
        username = (EditText)findViewById(R.id.username);
        this.setTitle("Chats - " +Singleton.getSingleton().getLogin());
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        isRunning = true;
        createSwipeLayout();
        lastMessage = new GetLastMessageRequest();
        try {
            lastMessage.start(getApplicationContext(), new SwipeRefreshLayout(getApplicationContext()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
            showChats();
            progressBar.setVisibility(View.GONE);
            swipeContainer.setVisibility(View.VISIBLE);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        new RefreshAsync().execute();
    }

    @Override
    protected void onPause(){
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showChats();
        isRunning = true;
        new RefreshAsync().execute();
    }

    public void startChat(View view) {
        if(username.getText().toString().length()>0 && !(username.getText().toString().equals(Singleton.getSingleton().getLogin()))){
            getPubKey(username.getText().toString());
        }
        else{
            Toast.makeText(getApplicationContext(), "Bitte richtigen Benutzernamen eintragen", Toast.LENGTH_SHORT).show();
        }
    }

    public void showChats(){
        for(String name: Message.getInstance().getConversations().keySet()){
            Log.i("Sender", name);
        }
        ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();
        for(String name: Message.getInstance().getConversations().keySet()){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(CONTACT, name);
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
                TextView txt = (TextView) list.findViewById(R.id.contact_name);
                Log.i("Adapter Txt", txt.getText().toString());
                getPubKey(txt.getText().toString());
        }
    });
    }

    protected void getPubKey(final String userName) {
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
                        startChatActivity(json.result.getString("pubkey_user"), userName);
                    }
                    if(mStatusCode==404){
                        Toast.makeText(getApplicationContext(), "User existiert nicht", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){

                }
                return json;
            }
        };
        RequestSingleton.getInstance(getApplicationContext()).add(request);
    }

    public void startChatActivity(final String pubkey, final String name){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ChatsActivity.this, SendActivity.class);
                intent.putExtra(USER, name);
                intent.putExtra(PUBKEY, pubkey);
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
                showChats();

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    class RefreshAsync extends AsyncTask<Void, Integer, String>
    {

        protected String doInBackground(Void...arg0) {
            while (isRunning){
                try{
                    Thread.sleep(5000);
                    Log.i("Update Messages", " after 5 secs.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showChats();
                        }
                    });
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
            return "";
        }

        protected void onPostExecute(String result) {

        }
    }
}
