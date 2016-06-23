package com.webwemser.classifiedapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
        this.setTitle("Chats");
    }

    public void startChat(View view) {
        if(username.getText().toString().length()>0){
            Intent intent = new Intent(this,SendActivity.class);
            intent.putExtra(USER,username.getText().toString());
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(), "Bitte Benutzernamen eintragen", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChats(ArrayList<String> names){
        ArrayList<HashMap<String, String>> chatList = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < names.size(); i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(CONTACT, names.get(i));
            chatList.add(map);
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
