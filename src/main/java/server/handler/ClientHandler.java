package server.handler;

import server.controller.FileSystemController;
import server.controller.UserController;

import java.io.*;
import java.util.*;

public class ClientHandler extends Thread
{

    private final ClientConnection clientConnection;

    private final FileSystemController fileSystemController;

    private final UserController userController;

    public ClientHandler(ClientConnection clientConnection, FileSystemController fileSystemController, UserController userController)
    {
        this.clientConnection = clientConnection;

        this.fileSystemController = fileSystemController;

        this.userController = userController;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("[Server] Client connected: " + clientConnection.clientSocket);

            // Handle client requests
            String request;

            while((request = clientConnection.receive()) != null)
            {
                System.out.println("[Server] Received request from client: " + request);

                // Process request and send response back to client
                String response = processRequest(request);

                clientConnection.send(response);

            }
        } catch(IOException e)
        {
            System.out.println("[Server] Error handling client request: " + e.getMessage());
        } finally
        {
            try
            {
                clientConnection.close(); // Close client socket

                System.out.println("[Server] Client connection closed: " + clientConnection);

            } catch(IOException e)
            {
                System.out.println("[Server] Error while closing client connection: " + e.getMessage());
            }
        }
    }

    private String processRequest(String request)
    {
        try
        {

            String[] parts = request.split(" ", 2);

            String command = parts[0]; // ["LIST","DOWNLOAD","START_SENDING","UPLOAD","DELETE"]

            String argument = parts.length > 1 ? parts[1] : null; // returns arguments if any
            System.out.println(argument);
            switch(command)
            {
                case "REGISTER":

                    boolean register = userController.registerUser(argument.split(",",2)[0],argument.split(",",2)[1]);

                    return register ? "true" : "false";

                case "LOGIN":

                    boolean login = userController.loginUser(argument.split(",",2)[0],argument.split(",",2)[1]);

                    return login ? "true" : "false";

                case "LIST":
                    Map<Integer, String> fileList = fileSystemController.listFiles();

                    return fileList.toString();

                case "DOWNLOAD":
                    // Example: DOWNLOAD indexOfFile
                    assert argument != null;

                    String response = fileSystemController.sendFileName(Integer.parseInt(argument));

                    return response;

                case "START_SENDING":
                    // for starting the sending of file when server receives confirmation from "DOWNLOAD"
                    assert argument != null;

                    boolean success = fileSystemController.sendFileToClient(argument);

                    return success ? "[Server] File downloaded successfully!" : "[Server] File not downloaded!";


                case "UPLOAD":
                    // Example: UPLOAD fileName
                    assert argument != null;

                    boolean uploaded = fileSystemController.startReceivingFileFromClient(argument);

                    return uploaded ? "[Server] File uploaded successfully!" : "[Server] File not uploaded!";


                case "DELETE":
                    // Example: DELETE indexOfFile
                    assert argument != null;

                    boolean deleted = fileSystemController.deleteFile(Integer.parseInt(argument));

                    return deleted ? "[Server] File deleted successfully!" : "[Server] Failed to delete file!";

                default:
                    return "[Server] Invalid command!";
            }
        } catch(AssertionError assertionError)
        {
            System.out.println("[Server] null value passed in choice!");

        } catch(NullPointerException e)
        {
            System.out.println("[Server] Error: Invalid request!");
        }

        return null;
    }
}
