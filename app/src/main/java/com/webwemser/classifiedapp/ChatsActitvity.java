package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatsActitvity extends AppCompatActivity {

    public static final String USER = "USERNAME", CONTACT = "CONTACT", KEY_POSITION = "POSITION";
    private EditText username;
    private ListView list;
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_actitvity);
        username = (EditText)findViewById(R.id.username);
        showChats();
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

    private void showChats(){
        ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < 10; i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(CONTACT, "CHAT "+i);
            chatList.add(map);
            Log.i("LOGGING: ", "Chat "+i);
        }
        list = (ListView)findViewById(R.id.list);
        // Getting adapter by passing xml data ArrayList
        adapter = new MyListAdapter(this, chatList);
        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatsActitvity.this, SendActivity.class);
                intent.putExtra(KEY_POSITION, position);
                startActivity(intent);
    }
    });
    }

}
