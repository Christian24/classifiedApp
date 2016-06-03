package com.webwemser.classifiedapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.KeyParameter;

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

register(userName.getText().toString(),password.getText().toString());
        }
    }
    protected void register(String userName, String password) {
        if(!userName.isEmpty() && !password.isEmpty()) {

            SecureRandom random = new SecureRandom();
            //The random bytes
            byte[]   bytes=  random.generateSeed(64);
            byte[] passwordBytes = password.getBytes();
            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            generator.init(passwordBytes,bytes,10000);
            byte[] masterkey = ((KeyParameter)  generator.generateDerivedParameters(256)).getKey();
            RSAKeyPairGenerator rsa = new RSAKeyPairGenerator();
            rsa.init(new KeyGenerationParameters(new SecureRandom(),2048));
            AsymmetricCipherKeyPair keys = rsa.generateKeyPair();
            CipherParameters privateKey = keys.getPrivate();
            CipherParameters publicKey = keys.getPublic();



        }
    }
}
