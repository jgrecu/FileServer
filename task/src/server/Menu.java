package server;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final Set<String> storage = new HashSet<>();


    public void run() {
        while (true) {
            String input = scanner.nextLine().strip();

            String[] parts = input.split("\\s+");
            String command = parts[0];
            String fileName = parts.length > 1 ? parts[1] : null;

            switch (command) {
                case "add":
                    addFile(fileName);
                    break;
                case "get":
                    getFile(fileName);
                    break;
                case "delete":
                    deleteFile(fileName);
                    break;
                case "exit":
                    scanner.close();
                    return;
                default:
                    break;
            }
        }

    }

    private void deleteFile(String fileName) {
        if (fileName != null && storage.remove(fileName)) {
            System.out.println("The file " + fileName + " was deleted");
        } else {
            System.out.println("The file " + fileName + " not found");
        }
    }

    private void getFile(String fileName) {
        if (fileName != null && storage.contains(fileName)) {
            System.out.println("The file " + fileName + " was sent");
        } else {
            System.out.println("The file " + fileName + " not found");
        }
    }

    private void addFile(String fileName) {
        if (fileName != null && fileName.matches("file[1-9]0?") && storage.add(fileName)) {
            System.out.println("The file " + fileName + " added successfully");
        } else {
            System.out.println("Cannot add the file " + fileName);
        }
    }
}
