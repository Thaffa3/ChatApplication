package com.app.chat;

public class LoginMessage extends Message {
    private final String username;
    private final String password;

    public LoginMessage(String username, String password) {
        super(username);
        this.username = username;
        this.password = password;
    }

    @Override
    public String getContent() {
        return null; // Login messages do not have a content field
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
