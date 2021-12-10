package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private final String address = "127.0.0.1";
    private final int port = 23456;

    public void start() {
        try (Socket socket = new Socket(InetAddress.getByName(address), port)
        ) {
            try (DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                System.out.println("Client started!");
                String stringText = "Give me everything you have!";
                output.writeUTF(stringText);
                System.out.println("Sent: " + stringText);
                String receivedMsg = input.readUTF();
                System.out.println("Received: " + receivedMsg);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
