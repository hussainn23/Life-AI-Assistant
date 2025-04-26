package com.softec.lifeaiassistant.models;

public class ChatDataClass {
    String text;
    boolean isResponse;

    public ChatDataClass(String text, boolean isResponse) {
        this.text = text;
        this.isResponse = isResponse;
    }

    public ChatDataClass() {
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
