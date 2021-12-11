package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Scanner scanner = new Scanner(System.in);
    private final String address = "127.0.0.1";
    private final int port = 23456;

    public void start() {
        try (Socket socket = new Socket(InetAddress.getByName(address), port)
        ) {
            try (DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                String stringText = "Give me everything you have!";
                System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");

                String option = scanner.nextLine().strip();

                switch (option) {
                    case "1":
                        stringText = get();
                        break;
                    case "2":
                        stringText = create();
                        break;
                    case "3":
                        stringText = delete();
                        break;
                    case "exit":
                        stringText = "exit";
                        break;
                    default:
                        break;
                }

                output.writeUTF(stringText);
                //System.out.println("-----Sent: " + stringText);
                System.out.println("The request was sent.");
                if (!"exit".equals(option)) {
                    String receivedMsg = input.readUTF();
                    //System.out.println("-----Received: " + receivedMsg);
                    processResponse(option, receivedMsg);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processResponse(String option, String receivedMsg) {
        switch (option) {
            case "1":
                if (receivedMsg.startsWith("200")) {
                    System.out.println("The content of the file is: " + receivedMsg.split("\\s+", 2)[1]);
                } else {
                    System.out.println("The response says that the file was not found!");
                }
                break;
            case "2":
                if (receivedMsg.startsWith("200")) {
                    System.out.println("The response says that the file was created!");
                } else {
                    System.out.println("The response says that creating the file was forbidden!");
                }
                break;
            case "3":
                if (receivedMsg.startsWith("200")) {
                    System.out.println("The response says that the file was successfully deleted!");
                } else {
                    System.out.println("The response says that the file was not found!");
                }
                break;
            default:
                break;
        }
    }

    private String get() {
        System.out.print("Enter filename: ");
        String fileName = scanner.nextLine().strip();
        return "GET " + fileName;
    }

    private String create() {
        System.out.print("Enter filename: ");
        String fileName = scanner.nextLine().strip();
        System.out.print("Enter file content: ");
        String fileContent = scanner.nextLine().strip();
        return "PUT " + fileName + " " + fileContent;
    }

    private String delete() {
        System.out.print("Enter filename: ");
        String fileName = scanner.nextLine().strip();
        return "DELETE " + fileName;
    }
}
