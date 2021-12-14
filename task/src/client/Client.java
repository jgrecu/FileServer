package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Client {
    private final Scanner scanner = new Scanner(System.in);
    private final String address = "127.0.0.1";
    private final int port = 23456;
    private final String rootPath = System.getProperty("user.dir") + File.separator +
            "src" + File.separator + "client" + File.separator + "data" + File.separator;

    public void start() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try (Socket socket = new Socket(InetAddress.getByName(address), port)
        ) {
            try (DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

                System.out.print("Enter action (1 - get a file, 2 - save the file, 3 - delete the file): ");

                String option = scanner.nextLine().strip();

                switch (option) {
                    case "1":
                        get(input, output);
                        break;
                    case "2":
                        create(input, output);
                        break;
                    case "3":
                        delete(input, output);
                        break;
                    case "exit":
                        output.writeUTF("exit");
                        System.out.println("The request was sent.");
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void get(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        String option = scanner.nextLine().strip();
        if ("1".equals(option)) {
            System.out.print("Enter filename: ");
            String fileName = scanner.nextLine().strip();
            dataOutputStream.writeUTF("GET BY_NAME " + fileName);
        }
        if ("2".equals(option)) {
            System.out.print("Enter id: ");
            String id = scanner.nextLine().strip();
            dataOutputStream.writeUTF("GET BY_ID " + id);
        }
        System.out.println("The request was sent.");
        String responseCode = dataInputStream.readUTF();
        if (responseCode.equals("200")) {
            int arraySize = dataInputStream.readInt();
            byte[] fileAsBytes = dataInputStream.readNBytes(arraySize);
            System.out.print("The file was downloaded! Specify a name for it: ");
            String fileName = scanner.nextLine().strip();
            Files.write(Path.of(rootPath, fileName), fileAsBytes);
            System.out.println("File saved on the hard drive!");
        } else {
            System.out.println("The response says that this file is not found!");
        }
    }

    private void create(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        System.out.print("Enter name of the file: ");
        String fileName = scanner.nextLine().strip();
        System.out.print("Enter name of the file to be saved on server: ");
        String serverFileName = scanner.nextLine().strip();
        if ("".equals(serverFileName)) {
            dataOutputStream.writeUTF("PUT");
        } else {
            dataOutputStream.writeUTF("PUT " + serverFileName);
        }
        FileInputStream fileInputStream = new FileInputStream(rootPath + fileName);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        byte[] fileAsBytes = bufferedInputStream.readAllBytes();
        dataOutputStream.writeInt(fileAsBytes.length);
        dataOutputStream.write(fileAsBytes);
        bufferedInputStream.close();
        System.out.println("The request was sent.");
        String[] response = dataInputStream.readUTF().split("\\s+");
        if ("200".equals(response[0])) {
            System.out.println("Response says that file is saved! ID = " + response[1]);
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    private void delete(DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
        String option = scanner.nextLine().strip();
        if ("1".equals(option)) {
            System.out.print("Enter filename: ");
            String fileName = scanner.nextLine().strip();
            dataOutputStream.writeUTF("DELETE BY_NAME " + fileName);
        }
        if ("2".equals(option)) {
            System.out.print("Enter id: ");
            String id = scanner.nextLine().strip();
            dataOutputStream.writeUTF("DELETE BY_ID " + id);
        }
        System.out.println("The request was sent.");
        String response = dataInputStream.readUTF();
        if ("200".equals(response)) {
            System.out.println("The response says that the file was successfully deleted!");
        } else {
            System.out.println("Response says that the file was not found!");
        }
    }
}
