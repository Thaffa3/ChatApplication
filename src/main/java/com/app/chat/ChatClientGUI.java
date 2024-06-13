package com.app.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ChatClientGUI {
    private JFrame frame;
    private JTextField textField;
    private JTextArea textArea;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String username;
    private Socket socket;

    public ChatClientGUI() {
        frame = new JFrame("Chat Client");
        textField = new JTextField();
        textArea = new JTextArea();

        frame.setLayout(new BorderLayout());
        frame.add(textField, BorderLayout.SOUTH);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (!text.isEmpty()) {
                    try {
                        outputStream.writeObject(new ChatMessage(username, text));
                        textField.setText("");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void connectToServer(String host, int port, String username, String password) {
        try {
            this.username = username;
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Send login message
            outputStream.writeObject(new LoginMessage(username, password));

            new Thread(new MessageReceiver()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    Message message = (Message) inputStream.readObject();
                    textArea.append(message.getSender() + ": " + message.getContent() + "\n");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClientGUI client = new ChatClientGUI();
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");
        client.connectToServer("localhost", 8080, username, password);
    }
}
