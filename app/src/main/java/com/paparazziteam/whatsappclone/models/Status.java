package com.paparazziteam.whatsappclone.models;

public class Status {

    private String id;
    private String idUser;
    private String comment;
    private String url;
    private long timestamp;
    private long timestampLimit;

    public Status() {
    }

    public Status(String id, String idUser, String comment, String url, long timestamp, long timestampLimit) {
        this.id = id;
        this.idUser = idUser;
        this.comment = comment;
        this.url = url;
        this.timestamp = timestamp;
        this.timestampLimit = timestampLimit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampLimit() {
        return timestampLimit;
    }

    public void setTimestampLimit(long timestampLimit) {
        this.timestampLimit = timestampLimit;
    }
}
