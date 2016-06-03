package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChatsActitvity extends AppCompatActivity {

    public static final String USER = "USERNAME";
    private EditText username;
    private ScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_actitvity);
        username = (EditText)findViewById(R.id.username);
        scroll = (ScrollView)findViewById(R.id.chat_scroll);
        this.setTitle("Chats");
    }

    public void startChat(View view) {
        Intent intent = new Intent(this,SendActivity.class);
        intent.putExtra(USER,username.getText().toString());
        startActivity(intent);
    }

}
