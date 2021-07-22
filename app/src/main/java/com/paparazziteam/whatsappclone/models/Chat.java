package com.paparazziteam.whatsappclone.models;

import java.util.ArrayList;

public class Chat {

    private String id;
    private long timestamp;
    private ArrayList<String> ids;
    private int numberMessages;
    private int idNotification;
    private String writing;

    public Chat() {
    }

    public Chat(String id, long timestamp, ArrayList<String> ids, int numberMessages, int idNotification, String writing) {
        this.id = id;
        this.timestamp = timestamp;
        this.ids = ids;
        this.numberMessages = numberMessages;
        this.idNotification = idNotification;
        this.writing = writing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getNumberMessages() {
        return numberMessages;
    }

    public void setNumberMessages(int numberMessages) {
        this.numberMessages = numberMessages;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }

    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }
}
