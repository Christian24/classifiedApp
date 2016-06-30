package com.webwemser.classifiedapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.webwemser.classifiedapp.requests.GetLastMessageRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.AESCBC;
import com.webwemser.classifiedapp.singleton.AESCBCResult;
import com.webwemser.classifiedapp.requests.Delete;
import com.webwemser.classifiedapp.singleton.Message;
import com.webwemser.classifiedapp.singleton.RSACipher;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public class SendActivity extends AppCompatActivity {

    public static final String SENDER = "SENDER", MESSAGE = "MESSAGE";
    private EditText message;
    private MyChatAdapter adapter;
    private ListView list;
    private String username, publicKey;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        message = (EditText)findViewById(R.id.message);
        username = getIntent().getStringExtra(ChatsActivity.USER);
        publicKey = getIntent().getStringExtra(ChatsActivity.PUBKEY);
        Log.i("Pubkey getIntent", publicKey);
        this.setTitle(username);
        message.setHint("Message to " + username);
        showMessages();
        isRunning = true;
        new RefreshAsync().execute();
    }

    @Override
    protected void onPause(){
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        isRunning = true;
        new RefreshAsync().execute();

    }

    public void send(View v){
        String m = message.getText().toString();
        if(m.length()>0){
            sendMessage(m);
            message.setText("");
        }
    }

    private void sendMessage(final String msg){
        try{
            Singleton singleton = Singleton.getSingleton();
            SecureRandom random = new SecureRandom();
            AESCBC aescbc = AESCBC.getInstance();
            byte[] key_recipient = random.generateSeed(16);
            AESCBCResult result = aescbc.encrypt(key_recipient,Helper.getBytes(msg));

            byte[] message_enc = result.getData();
            String messageString = Helper.base64Encoding(message_enc);
            byte[] iv = result.getIv();
            String iv_string = Helper.base64Encoding(iv);

            PublicKey rsaPublicKey = Helper.getKeyFromPEM(publicKey);

            RSACipher rsaCipher = RSACipher.getInstance();
            byte[] key_recipient_enc = rsaCipher.encrypt(rsaPublicKey,key_recipient);
            String key_recipient_encString =Helper.base64Encoding(key_recipient_enc);
            String timestamp = Integer.toString(Helper.getTimestamp());

            String sig_recipient_StringToProcess = new String(singleton.getLogin()+ messageString + iv_string + key_recipient_encString);
            byte[] sig_recipient = Helper.generateSignature(singleton.getPrivate_key(),sig_recipient_StringToProcess);
            String sig_recipient_String = Helper.base64Encoding(sig_recipient);

            String sig_serviceToProcess= new String(singleton.getLogin()+ messageString + iv_string + key_recipient_encString + sig_recipient_String + timestamp + username);
            byte[] sig_service = Helper.generateSignature(singleton.getPrivate_key(), sig_serviceToProcess);
            String sig_service_String = Helper.base64Encoding(sig_service);

            boolean correct = Helper.verifySignature(Singleton.getSingleton().getPubkey(),sig_serviceToProcess.getBytes(),sig_service);
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("sender", Singleton.getSingleton().getLogin());
            Log.i("sender", Singleton.getSingleton().getLogin());
            params.put("content_enc", messageString);
            Log.i("content_enc", messageString);
            params.put("key_recipient_enc", key_recipient_encString);
            Log.i("key_recipient_enc", key_recipient_encString);
            params.put("iv", iv_string);
            Log.i("iv", iv_string);
            params.put("sig_recipient", sig_recipient_String);
            Log.i("sig_recipient", sig_recipient_String);
            params.put("timestamp", timestamp);
            Log.i("Timestamp", timestamp);
            params.put("sig_service", sig_service_String);
            Log.i("sig_service", sig_service_String);
            params.put("recipient", username);
            Log.i("recipient", username);
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
                    MessageObject messageObject = new MessageObject(Singleton.getSingleton().getLogin(),msg);
                    Message.getInstance().addMessageSelf(messageObject,username);
                    return super.parseNetworkResponse(response);
                }
            };
            RequestSingleton.getInstance(getApplicationContext()).add(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showMessages(){
        ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();

        for(int i = 0; i < Message.getInstance().getConversations().get(username).size(); i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(SENDER, Message.getInstance().getConversations().get(username).get(i).getSender());
            map.put(MESSAGE, Message.getInstance().getConversations().get(username).get(i).getMessage());
            chatList.add(map);
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
                                Delete.DeleteMessage(username);
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

    class RefreshAsync extends AsyncTask<Void, Integer, String>
    {

        protected String doInBackground(Void...arg0) {
            while (isRunning){
                try{
                    Thread.sleep(2000);
                    Log.i("Update Messages", " now");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMessages();
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
