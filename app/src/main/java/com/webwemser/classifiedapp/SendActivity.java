package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONObject;

import java.util.HashMap;

public class SendActivity extends AppCompatActivity {

    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        message = (EditText)findViewById(R.id.message);
        Intent intent = getIntent();
        String username = intent.getStringExtra(ChatsActivity.USER);
        this.setTitle(username);
        message.setHint("Message to " + username);
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

    	Button sendButton = (Button)findViewById(R.id.send);
        Singleton singletonInstance = Singleton.getSingleton();

        JSONObject json = new JSONObject(params);
    }


    public void sendMessage(String message){

    }


}
