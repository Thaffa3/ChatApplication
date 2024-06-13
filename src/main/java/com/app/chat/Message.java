package com.app.chat;

import java.io.Serializable;

public abstract class Message implements Serializable {
    private final String sender;

    public Message(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public abstract String getContent();
}
