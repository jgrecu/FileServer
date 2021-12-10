package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final String address = "127.0.0.1";
    private final int port = 23456;

    public void start() {
        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address))
        ) {
            Socket socket = server.accept();
            System.out.println("Server started!");
            try (DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                String receivedMsg = input.readUTF();
                System.out.println("Received: " + receivedMsg);
                String sendMsg = "All files were sent!";
                output.writeUTF(sendMsg);
                System.out.println("Sent: " + sendMsg);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
