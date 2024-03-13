package com.remoteFS.server.handler;

import com.remoteFS.server.controller.FileSystem;
import com.remoteFS.server.controller.User;

import java.io.*;

public class ClientHandler extends Thread
{

    private final ClientConnection clientConnection;

    private final FileSystem fileSystemController;

    private final User userController;

    public ClientHandler(ClientConnection clientConnection, FileSystem fileSystemController, User userController)
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
            var request = "";

            while((request = clientConnection.receive()) != null)
            {
                System.out.println("[Server] Received request from client: " + request);

                // Process request and send response back to client
                var response = processRequest(request);

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

            var parts = request.split(" ", 2);

            var command = parts[0]; // ["LIST","DOWNLOAD","START_SENDING","UPLOAD","DELETE"]

            var argument = parts.length > 1 ? parts[1] : null; // returns arguments if any

            switch(command)
            {
                case "REGISTER":

                    var register = userController.registerUser(argument.split(",",2)[0],argument.split(",",2)[1]);

                    return register ? "true" : "false";

                case "LOGIN":

                    var login = userController.loginUser(argument.split(",",2)[0],argument.split(",",2)[1]);

                    return login ? "true" : "false";

                case "LIST":
                    var fileList = fileSystemController.listFiles();

                    return fileList.toString();

                case "DOWNLOAD":
                    // Example: DOWNLOAD indexOfFile

                    var response = fileSystemController.sendFileName(Integer.parseInt(argument));

                    return response;

                case "START_SENDING":
                    // for starting the sending of file when server receives confirmation from "DOWNLOAD"

                    var success = fileSystemController.sendFileToClient(argument);

                    return success ? "[Server] File downloaded successfully!" : "[Server] File not downloaded!";


                case "UPLOAD":
                    // Example: UPLOAD fileName

                    var uploaded = fileSystemController.startReceivingFileFromClient(argument);

                    return uploaded ? "[Server] File uploaded successfully!" : "[Server] File not uploaded!";


                case "DELETE":
                    // Example: DELETE indexOfFile

                    var deleted = fileSystemController.deleteFile(Integer.parseInt(argument));

                    return deleted ? "[Server] File deleted successfully!" : "[Server] Failed to delete file!";

                default:
                    return "[Server] Invalid command!";
            }
        } catch(NullPointerException e)
        {
            System.out.println("[Server] Error: Invalid request!");
        }

        return null;
    }
}
