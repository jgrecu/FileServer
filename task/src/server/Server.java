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
    private String fileName;
    private String fileContents;

    public void start() {
        try (ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address))
        ) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = server.accept();
                try (DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                    String receivedMsg = input.readUTF();
                    String[] parts = receivedMsg.split("\\s+", 3);
                    Commands command = Commands.valueOf(parts[0]);
                    if (parts.length == 2) {
                        fileName = parts[1];
                    } else if (parts.length > 2) {
                        fileName = parts[1];
                        fileContents = parts[2];
                    }
                    if (command == Commands.exit) {
                        return;
                    }
                    System.out.println("Received: " + receivedMsg);
                    String responseCode = processFile(command);
                    output.writeUTF(responseCode);
                    System.out.println("Sent: " + responseCode);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String processFile(Commands command) {
        switch (command) {
            case GET:
                if (fileName == null) {
                    return "404";
                }
                fileName = null;
                fileContents = null;
                break;
            case PUT:
                if (fileName == null && fileContents == null) {
                    return "403";
                }
                fileName = null;
                fileContents = null;
                break;
            case DELETE:
                if (fileName == null && fileContents == null) {
                    return "404";
                }
                fileName = null;
                fileContents = null;
                break;
        }
        return "503";
    }

}
