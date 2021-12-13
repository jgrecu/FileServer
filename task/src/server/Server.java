package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Path ROOT_PATH = Path.of(System.getProperty("user.dir"), "src", "server", "data");
    protected static FileServer fileServer;
    private final String address = "127.0.0.1";
    private final int port = 23456;
    private ExecutorService executor;
    private ServerSocket serverSocket;

    private void initialize() {
        fileServer = new FileServer(ROOT_PATH);
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        initialize();

        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
            System.out.println("Server started!");

            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(() -> handleClientRequest(socket));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void stop() {
        executor.shutdownNow();
        fileServer.stop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void handleClientRequest(Socket socket) {
        DataInputStream input = null;
        DataOutputStream output = null;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            String received = input.readUTF();
            System.out.println(LocalTime.now() + " - Received: " + received);
            String[] parts = received.split("\\s+");
            Commands command = Commands.valueOf(parts[0]);
            Integer fileId = null;
            String filename = "";
            boolean byId = false;

            if (command == Commands.GET || command == Commands.DELETE) {
                String byNameOrId = parts[1];
                byId = "BY_ID".equals(byNameOrId);
                if (byId) {
                    fileId = Integer.parseInt(parts[2]);
                } else {
                    filename = parts[2];
                }
            }

            byte[] fileContentBinary = null;
            String response = "";

            switch (command) {
                case PUT:
                    int size = input.readInt();
                    fileContentBinary = new byte[size];
                    input.readFully(fileContentBinary, 0, size);
                    fileId = fileServer.put(filename, fileContentBinary);
                    response = "" + (fileId > 0 ? "200" + " " + fileId : "403");
                    break;
                case GET:
                    fileContentBinary = byId ? fileServer.get(fileId) : fileServer.get(filename);
                    response = "" + (fileContentBinary == null ? "404" : "200");
                    break;
                case DELETE:
                    boolean res = byId ? fileServer.delete(fileId) : fileServer.delete(filename);
                    response = "" + (res ? "200" : "404");
                    break;
                case exit:
                    stop();
                    break;
                default:
                    break;
            }

            output.writeUTF(response);
            if (command == Commands.GET && fileContentBinary != null) {
                output.writeInt(fileContentBinary.length);
                output.write(fileContentBinary);
            }
            System.out.println(LocalTime.now() + " - Sent: " + response);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());;
            }
        }
    }
}