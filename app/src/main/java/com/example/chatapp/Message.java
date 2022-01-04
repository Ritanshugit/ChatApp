package com.example.chatapp;

public class Message {

    String sendType, text;

    public Message(String sendType, String text) {
        this.sendType = sendType;
        this.text = text;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
