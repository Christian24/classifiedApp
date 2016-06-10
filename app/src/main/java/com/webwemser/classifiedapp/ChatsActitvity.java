package com.webwemser.classifiedapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
        View.OnClickListener myhandler = new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Klick", Toast.LENGTH_SHORT).show();
            }
        };



    }

    public void startChat(View view) {
        Intent intent = new Intent(this,SendActivity.class);
        intent.putExtra(USER,username.getText().toString());
        startActivity(intent);
    }

}
