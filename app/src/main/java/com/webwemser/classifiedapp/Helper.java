package com.webwemser.classifiedapp;

import java.nio.charset.StandardCharsets;

/**
 * Created by Christian on 03.06.2016.
 */
public class Helper {
    public static final String URL = "https://webengserver.herokuapp.com/";
    public static String getStringFromBytes(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
