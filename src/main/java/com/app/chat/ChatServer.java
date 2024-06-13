package com.app.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final DatabaseManager databaseManager = new DatabaseManager();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(Message message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void sendPrivateMessage(PrivateChatMessage message) {
        ClientHandler recipient = getClientByUsername(message.getRecipient());
        if (recipient != null) {
            recipient.sendMessage(message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected: " + client.getUsername());
        broadcastMessage(new ChatMessage("Server", client.getUsername() + " has left the chat"));
    }

    public ClientHandler getClientByUsername(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return client;
            }
        }
        return null;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static void main(String[] args) {
        int port = 8080; // Change this to the desired port number
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
