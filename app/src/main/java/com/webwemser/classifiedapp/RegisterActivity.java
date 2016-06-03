package com.webwemser.classifiedapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;

import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;

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
    protected void register(String userName, String password) {
        if(!userName.isEmpty() && !password.isEmpty()) {

            SecureRandom random = new SecureRandom();
            //The random bytes
            byte[]   bytes=  random.generateSeed(64);
            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            

        }
    }
}
