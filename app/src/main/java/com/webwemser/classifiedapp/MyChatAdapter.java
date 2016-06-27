package com.webwemser.classifiedapp;

/**
 * Created by Jannik W. on 26.04.2016.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyChatAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;


    public MyChatAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;


        HashMap<String, String> meet;
        meet = data.get(position);

        TextView message;
        Log.i("SENDER", meet.get(SendActivity.SENDER));
        if(meet.get(SendActivity.SENDER).equals("Self")){
            vi = inflater.inflate(R.layout.message_sent, null);
            message = (TextView)vi.findViewById(R.id.textSent);
        }
        else {
            vi = inflater.inflate(R.layout.message_received, null);
            message = (TextView)vi.findViewById(R.id.textReceived);
        }
        // Setting all values in listview
        message.setText(meet.get(SendActivity.MESSAGE));
        return vi;
    }
}
