package server;

import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler extends Thread
{

    private final ClientConnection clientConnection;

    private final FileSystemManager fileSystemManager;

    public ClientHandler(ClientConnection clientConnection, FileSystemManager fileSystemManager)
    {
        this.clientConnection = clientConnection;
        this.fileSystemManager = fileSystemManager;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Client connected: " + clientConnection.clientSocket);
            // Handle client requests
            String request;
            while((request = clientConnection.receive()) != null)
            {
                System.out.println("Received request from client: " + request);

                // Process request and send response back to client
                String response = processRequest(request);

                clientConnection.send(response);
            }
        } catch(IOException e)
        {
            System.out.println("Error handling client request: " + e.getMessage());
        } finally
        {
            try
            {
                clientConnection.close(); // Close client socket
                System.out.println("Client connection closed: " + clientConnection);
            } catch(IOException e)
            {
                System.out.println("Error while closing client connection: " + e.getMessage());
            }
        }
    }

    private String processRequest(String request)
    {
        try
        {

            String[] parts = request.split(" ", 2);
            String command = parts[0];
            String argument = parts.length > 1 ? parts[1] : null;

            switch(command)
            {
                case "LIST":
                    Map<Integer, String> fileList = fileSystemManager.listFiles();
                    return fileList.toString();

                case "DOWNLOAD":
                    // Example: DOWNLOAD indexOfFile
                    assert argument != null;
                    boolean downloaded = fileSystemManager.sendFileToClient(Integer.parseInt(argument));
                    return downloaded ? "File downloaded successfully" : "Failed to download file";

                case "UPLOAD":
                    // Example: UPLOAD /path/to/file.txt
                    boolean uploaded = fileSystemManager.receiveFileFromClient(argument);
                    return uploaded ? "File uploaded successfully" : "Failed to upload file";

                case "DELETE":
                    // Example: DELETE indexOfFile
                    assert argument != null;

                    boolean deleted = fileSystemManager.deleteFile(Integer.parseInt(argument));

                    return deleted ? "File deleted successfully" : "Failed to delete file";

                default:
                    return "Invalid command";
            }
        } catch(AssertionError assertionError)
        {
            System.out.println("null value passed in choice!");
        } catch(NullPointerException e)
        {
            System.out.println("Error: Invalid request!!");
        }

        return null;
    }
}
