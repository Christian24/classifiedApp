package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SendActivity extends AppCompatActivity {

    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        message = (EditText)findViewById(R.id.message);
        Intent intent = getIntent();
        String username = intent.getStringExtra(ChatsActitvity.USER);
        this.setTitle(username);
        sendMessage("Hey there!");
        message.setHint("Message to " + username);
    }

    public void sendMessage(String message){
        /*
        LinearLayout linear = (LinearLayout)findViewById(R.id.layout1);
        TextView txt = (TextView)findViewById(R.id.textView);
        txt.setText(message);
        linear.addView(txt);
        rel.addView(linear);
        */
    }
}
