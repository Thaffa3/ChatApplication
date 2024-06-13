package com.app.chat;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final ChatServer server;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

            handleClient();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient() {
        try {
            while (true) {
                Message message = (Message) inputStream.readObject();
                if (message instanceof LoginMessage) {
                    LoginMessage loginMessage = (LoginMessage) message;
                    username = loginMessage.getUsername();
                    if (server.getDatabaseManager().authenticateUser(loginMessage.getUsername(), loginMessage.getPassword())) {
                        outputStream.writeObject(new ChatMessage("Server", "Login successful"));
                        server.broadcastMessage(new ChatMessage("Server", username + " has joined the chat"));
                    } else {
                        outputStream.writeObject(new ChatMessage("Server", "Login failed"));
                        break;
                    }
                } else if (message instanceof ChatMessage) {
                    server.broadcastMessage(message);
                } else if (message instanceof PrivateChatMessage) {
                    PrivateChatMessage privateMessage = (PrivateChatMessage) message;
                    server.sendPrivateMessage(privateMessage);
                }
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected: " + username);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
        }
    }

    public void sendMessage(Message message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}
