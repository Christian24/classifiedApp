package com.webwemser.classifiedapp.requests;

import android.app.Application;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Created by Christian on 23.06.2016.
 */
public class RequestSingleton extends Application {
    private static RequestSingleton instance;
    private RequestQueue queue;
    private RequestSingleton() {

        RequestQueue mRequestQueue;

// Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        queue = new RequestQueue(cache, network);

// Start the queue
        queue.start();

    }
    public void add(Request request) {
        queue.add(request);
    }
    public static RequestSingleton getInstance() {
        if(instance == null)
            instance = new RequestSingleton();

        return instance;
    }
}
