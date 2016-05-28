package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChatsActitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_actitvity);
    }
    public void startChat(View view) {
        Intent intent = new Intent(this,SendActivity.class);
        startActivity(intent);
    }
}
