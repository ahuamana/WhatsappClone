package com.paparazziteam.whatsappclone.models;

public class Message {

    private String id;
    private String idSender;
    private String idReceiver;
    private String idChat;
    private String message;
    private String status;
    private Long timestamp;

    public Message() {
    }

    public Message(String id, String idSender, String idReceiver, String idChat, String message, String status, Long timestamp) {
        this.id = id;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.idChat = idChat;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}