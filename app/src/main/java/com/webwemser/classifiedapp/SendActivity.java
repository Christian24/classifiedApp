package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Intent intent = getIntent();
        String username = intent.getStringExtra(ChatsActitvity.USER);
        this.setTitle(username);
    }
}
