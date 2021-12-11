package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

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
                        socket.close();
                        return;
                    }
                    //System.out.println("Received: " + receivedMsg);
                    String responseCode = processFile(command);
                    output.writeUTF(responseCode);
                    //System.out.println("Sent: " + responseCode);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private String processFile(Commands command) {
        String response = "";
        switch (command) {
            case GET:
                if (fileName == null) {
                    return "404";
                }
                 response = get(fileName);
                fileName = null;
                fileContents = null;
                return response;

            case PUT:
                if (fileName == null && fileContents == null) {
                    return "403";
                }
                 response = put(fileName, fileContents);
                fileName = null;
                fileContents = null;
                return response;
            case DELETE:
                if (fileName == null && fileContents == null) {
                    return "404";
                }
                response = delete(fileName);
                fileName = null;
                fileContents = null;
                return response;
        }
        return "400";
    }

    private String get(String fileName) {
        Path root = Path.of(System.getProperty("user.dir"), "src", "server", "data", fileName);
        //System.out.println(root);
        if (Files.exists(root)) {
            try {
                fileContents = new String(Files.readAllBytes(root));
                return "200 " + fileContents;
            } catch (IOException e) {
                return "404";
            }

        } else {
            return "404";
        }
    }

    private String put(String fileName, String fileContents) {
        Path root = Path.of(System.getProperty("user.dir"), "src", "server", "data", fileName);
        //System.out.println(root);
        if (Files.notExists(root)) {
            try {
                Files.write(root, fileContents.getBytes());
                return "200";
            } catch (IOException e) {
                return "403";
            }

        } else {
            return "403";
        }
    }

    private String delete(String fileName) {
        Path root = Path.of(System.getProperty("user.dir"), "src", "server", "data", fileName);
        //System.out.println(root + " -> " + Files.exists(root));
        if (Files.exists(root)) {
            try {
                Files.deleteIfExists(root);
                return "200";
            } catch (IOException e) {
                return "404";
            }
        } else {
            return "404";
        }
    }

}
