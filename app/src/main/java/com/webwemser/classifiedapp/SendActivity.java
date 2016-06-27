package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendActivity extends AppCompatActivity {

    public static final String SENDER = "SENDER", MESSAGE = "MESSAGE";
    private EditText message;
    private MyChatAdapter adapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        message = (EditText)findViewById(R.id.message);
        Intent intent = getIntent();
        String username = intent.getStringExtra(ChatsActivity.USER);
        this.setTitle(username);
        message.setHint("Message to " + username);
        showChats();
    }

    public void sendMessage(EditText message) {

        HashMap<String,String> params = new HashMap<String,String>();
    	String number = Helper.getRandomString(64);
    	String content = message.getText().toString();


        params.put("sender","senderMock");
        params.put("content_enc","content_encMock");
        params.put("key_recipient_enc","key_recipient_encMock");
        params.put("sig_recipient","sig_recipientMock");
        params.put("sender","senderMock");
        Singleton singletonInstance = Singleton.getSingleton();

        JSONObject json = new JSONObject(params);
    }


    public void sendMessage(String message){

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
