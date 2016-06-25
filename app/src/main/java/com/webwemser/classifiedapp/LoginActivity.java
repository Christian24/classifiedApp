package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.Singleton;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void startRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void startLogin(View view) {
        // Intent intent = new Intent(this,ChatsActitvity.class);
        // startActivity(intent);
        EditText userName = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        if (userName.getText() != null && password.getText() != null) {
            login(userName.getText().toString(), password.getText().toString());
        }
    }

    protected void login(final String userName, final String password) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Helper.URL + userName, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("Log Response ", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String body;
                //get status code here
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                Log.i("Log VolleyError", statusCode);
                if (error.networkResponse.data != null) {
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                        Log.i("Log VolleyError", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;

                Response<JSONObject> json = super.parseNetworkResponse(response);
                try {
                    Singleton instance = Singleton.getSingleton();
                    String salt_masterkey = json.result.getString("salt_masterkey");
                    Log.i("Salt_Masterkey", salt_masterkey);
                    instance.setSalt_masterkey(salt_masterkey);

                    String pubkey_user = json.result.getString("pubkey_user");
                    instance.setPubkey(pubkey_user);
                    Log.i("Pubkey_user", pubkey_user);
                    String privkey_user_enc = json.result.getString("privkey_user_enc");
                    byte[] privkey = Base64.decode(privkey_user_enc, Base64.DEFAULT);
                    instance.setPrivate_key_enc(Helper.getStringFromBytes(privkey));
                    byte[] passwordBytes = password.getBytes();
                    PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
                    generator.init(passwordBytes, privkey, 10000);
                    byte[] masterkey = ((KeyParameter) generator.generateDerivedParameters(256)).getKey();
                    instance.setMasterkey(Helper.getStringFromBytes(masterkey));
                    String x = new String(masterkey, "UTF-8");
                    Log.i("Masterkey", x);
                    SecretKeySpec secretKeySpec = Helper.buildKey(instance.getMasterkey().getBytes());
                    //SecretKeySpec secretKeySpec = new SecretKeySpec(instance.getMasterkey().getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                    instance.setPrivate_key(Helper.getStringFromBytes(cipher.doFinal(instance.getPrivate_key_enc().getBytes())));
                    instance.setLogin(userName);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }

                return json;
            }
        };

        RequestSingleton.getInstance(getApplicationContext()).add(request);
    }






}
