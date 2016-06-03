package com.webwemser.classifiedapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.RSAKeyGenerationParameters;
import org.spongycastle.crypto.tls.EncryptionAlgorithm;
import org.spongycastle.util.encoders.Base64Encoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void register(View view) throws NoSuchAlgorithmException {
        EditText password   = (EditText)findViewById(R.id.password);
        EditText userName = (EditText)findViewById(R.id.username);
        if(!userName.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            //Username and Password are present

register(userName.getText().toString(),password.getText().toString());
        }
    }
    protected void register(String userName, String password) throws NoSuchAlgorithmException {
        if(!userName.isEmpty() && !password.isEmpty()) {

            SecureRandom random = new SecureRandom();
            //The random bytes
            byte[]   bytes=  random.generateSeed(64);
            byte[] passwordBytes = password.getBytes();
            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            generator.init(passwordBytes,bytes,10000);
            byte[] masterkey = ((KeyParameter)  generator.generateDerivedParameters(256)).getKey();

            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(2048);

            KeyPair keys = rsa.generateKeyPair();
            PrivateKey privateKey = keys.getPrivate();
            PublicKey publicKey = keys.getPublic();
            String privateString = keys.getPublic().getEncoded().toString();
            SecretKeySpec secretKeySpec = new SecretKeySpec(privateKey.getEncoded(),"AES");
            try
            {
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
               byte[] private_key_enc= cipher.doFinal(publicKey.getEncoded());
                HashMap<String,String> params = new HashMap<String,String>();
                params.put("login",userName);
                params.put("salt_masterkey",bytes.toString());
                params.put("pubkey_user",publicKey.getEncoded().toString());
               Base64Encoder encoder = new Base64Encoder();
              String privKeyToSendEnc=  Base64.encodeToString(private_key_enc,Base64.DEFAULT);
               params.put("privkey_user_enc",privKeyToSendEnc);
                JSONObject json = new JSONObject(params);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,Helper.URL,json,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }


                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                RequestQueue mRequestQueue;

// Instantiate the cache
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
                mRequestQueue = new RequestQueue(cache, network);

// Start the queue
                mRequestQueue.start();
                mRequestQueue.add(request);
            }catch (Exception e) {

            }




        }
    }
}
