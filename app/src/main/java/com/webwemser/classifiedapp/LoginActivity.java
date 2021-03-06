package com.webwemser.classifiedapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webwemser.classifiedapp.requests.RequestSingleton;
import com.webwemser.classifiedapp.singleton.AESECB;
import com.webwemser.classifiedapp.singleton.Singleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    private EditText userName, password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar)findViewById(R.id.progess_login);
    }

    @Override
    protected void onPause(){
        super.onPause();
        hideProgressbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressbar();
    }

    public void startRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void startLogin(View view) {
        if (userName.getText() != null && password.getText() != null) {
            progressBar.setVisibility(View.VISIBLE);
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
                if(error.networkResponse!=null) {
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
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.i("Statuscode", response.statusCode+"");
                Response<JSONObject> json = super.parseNetworkResponse(response);
                try {
                    Singleton instance = Singleton.getSingleton();
                    byte[] salt_masterkey = Helper.base64Decoding( json.result.getString("salt_masterkey"));

                    instance.setSalt_masterkey(salt_masterkey);
                    String pubkey_user = json.result.getString("pubkey_user");
                    instance.setPubkey(Helper.getKeyFromPEM(pubkey_user));
                    // Log.i("Pubkey_user", pubkey_user);
                    String privkey_user_enc = json.result.getString("privkey_user_enc");
                    byte[] privkey = Helper.base64Decoding(privkey_user_enc);
                    // byte[] privkey = Helper.getBytes(privkey_user_string);
                    instance.setPrivate_key_enc(privkey);
                    byte[] passwordBytes = password.getBytes();
                    PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
                    generator.init(passwordBytes,salt_masterkey, 10000);
                    final byte[] masterkey = ((KeyParameter)generator.generateDerivedParameters(256)).getKey();

                    String x = new String(masterkey, "UTF-8");
                    Log.i("Masterkey", x);
                    SecretKeySpec secretKeySpec = Helper.buildKey(masterkey);

                    AESECB aesecb = AESECB.getInstance();
                    byte[] privateBytes = aesecb.decrypt(masterkey,privkey);

                    PrivateKey privateKey = Helper.generatePrivateKey(privateBytes);
                    instance.setPrivate_key(privateKey);
                    instance.setLogin(userName);

                    if(mStatusCode==200 || mStatusCode==304){
                        startChatActivity();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }  catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }

                return json;
            }
        };
        RequestSingleton.getInstance(getApplicationContext()).add(request);
    }

    private void hideProgressbar(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void startChatActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Erfolgreich angemeldet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, ChatsActivity.class);
                intent.putExtra(RegisterActivity.USERNAME, userName.getText().toString());
                startActivity(intent);
            }
        });
    }
}
