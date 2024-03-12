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

                    String response = fileSystemManager.sendFileName(Integer.parseInt(argument));

                    return response;

                case "START_SENDING":
                    assert argument != null;

                    boolean success = fileSystemManager.sendFileToClient(argument);

                    return success ? "[Server] File downloaded successfully!" : "[Server] File not downloaded!";


                case "UPLOAD":
                    // Example: START_RECEIVING fileName
                    assert argument != null;

                    boolean uploaded = fileSystemManager.startReceivingFileFromClient(argument);

                    return uploaded ? "[Server] File uploaded successfully!" : "[Server] File not uploaded!";


                case "DELETE":
                    // Example: DELETE indexOfFile
                    assert argument != null;

                    boolean deleted = fileSystemManager.deleteFile(Integer.parseInt(argument));

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
