package com.webwemser.classifiedapp;

import java.util.Date;

/**
 * Created by Christian on 30.06.2016.
 */
public class MessageObject {
    protected int id;
    protected String sender;
    protected String message;
    protected Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MessageObject(int id, String sender, String message) {
        this.id = id;
        this.sender = sender;
        this.message = message;
    }
}
