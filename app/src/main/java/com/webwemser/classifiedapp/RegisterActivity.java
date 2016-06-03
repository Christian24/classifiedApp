package com.webwemser.classifiedapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void register(View view) {
        EditText password   = (EditText)findViewById(R.id.password);
        EditText userName = (EditText)findViewById(R.id.username);
        if(!userName.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            //Username and Password are present
           

        }
    }
}
