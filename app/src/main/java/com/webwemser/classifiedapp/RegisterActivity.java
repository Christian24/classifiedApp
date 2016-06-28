package com.webwemser.classifiedapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Singleton;
import org.json.JSONObject;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {

    public static final String USERNAME = "USERNAME";
    private EditText password, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        password   = (EditText)findViewById(R.id.password);
        username = (EditText)findViewById(R.id.username);
    }

    public void register(View view) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            //Username and Password are present
            register(username.getText().toString(),password.getText().toString());
        }
    }
    protected void register(final String userName, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if(!userName.isEmpty() && !password.isEmpty()) {
            final SecureRandom random = new SecureRandom();

            //The random bytes
            byte[]   bytes= random.generateSeed(64); //Helper.getBytes(Helper.getRandomString(64));
            Log.i("Salt_masterkey_Register",Helper.getString(bytes));
            byte[] passwordBytes = Helper.getBytes(password);
            PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
            generator.init(passwordBytes,bytes,10000);


            final byte[] masterkey = ((KeyParameter)  generator.generateDerivedParameters(256)).getKey();
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(2048);

            KeyPair keys = rsa.generateKeyPair();
            PrivateKey privateKey = keys.getPrivate();
            Singleton.getSingleton().setPrivate_key(privateKey);
            PublicKey publicKey = keys.getPublic();


            SecretKeySpec secretKeySpec = null;
            try {
                secretKeySpec = Helper.buildKey(masterkey);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] private_key_enc;
            try
            {
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
                private_key_enc= cipher.doFinal(privateKey.getEncoded());


                HashMap<String,String> params = new HashMap<String,String>();
                params.put("login",userName);

                params.put("salt_masterkey",Helper.getString(Helper.base64Encoding(bytes)));

                Singleton.getSingleton().setSalt_masterkey(bytes);

                Singleton.getSingleton().setLogin(userName);

                params.put("pubkey_user",Helper.getPEMStringFromKey(publicKey));

                Singleton.getSingleton().setPubkey(publicKey);

                String privKeyToSendEnc= Helper.getString( Helper.base64Encoding(private_key_enc));
                Singleton.getSingleton().setPrivate_key_enc(private_key_enc);

                params.put("privkey_user_enc",privKeyToSendEnc);
                JSONObject json = new JSONObject(params);
                Uri url = Helper.getUriBuilder().appendPath(userName).build();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url.toString(),json,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Log Response ", response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body;
                        //get status code here
                        if(error.networkResponse!=null){
                            String statusCode = String.valueOf(error.networkResponse.statusCode);
                            //get response body and parse with appropriate encoding
                            Log.i("Log VolleyError", statusCode);
                            if(error.networkResponse.data!=null) {
                                try {
                                    body = new String(error.networkResponse.data,"UTF-8");
                                    Log.i("Log VolleyError", body);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                })
                {
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        Log.i("Log ParseResponse", response.statusCode+"");
                        int mStatusCode = response.statusCode;
                        if(mStatusCode==201){
                            Singleton instance = Singleton.getSingleton();
                            instance.setMasterkey(masterkey);
                            startChatActivity();
                        }
                        return super.parseNetworkResponse(response);
                    }
                };

                RequestSingleton.getInstance(getApplicationContext()).add(request);

            }catch (Exception e) {
                Log.i("Log ", e.getMessage());
            }
        }
    }

    public void startChatActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Erfolgreich registriert", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, ChatsActivity.class);
                intent.putExtra(USERNAME, username.getText().toString());
                startActivity(intent);
            }
        });
    }
}