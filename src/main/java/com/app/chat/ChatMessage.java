package com.app.chat;

public class ChatMessage extends Message {
    private final String content;

    public ChatMessage(String sender, String content) {
        super(sender);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
