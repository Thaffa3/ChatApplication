package com.app.chat;

public class PrivateChatMessage extends Message {
    private final String recipient;
    private final String content;

    public PrivateChatMessage(String sender, String recipient, String content) {
        super(sender);
        this.recipient = recipient;
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    public String getRecipient() {
        return recipient;
    }
}
