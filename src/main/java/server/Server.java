package server;

import server.controller.UserController;
import server.handler.ClientConnection;
import server.handler.ClientHandler;
import server.controller.FileSystemController;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server
{
    private static final int PORT = ServerConfig.PORT;

    public static void main(String[] args)
    {
        ServerSocket serverSocket = null;

        ExecutorService executorService = null;

        try
        {

            serverSocket = new ServerSocket(PORT);

            System.out.println("[Server] Server socket open at port: " + PORT);

            executorService = Executors.newCachedThreadPool();

            while(true)
            {
                Socket clientSocket = serverSocket.accept();

                ClientConnection clientConnection = new ClientConnection(clientSocket);

                FileSystemController fileSystemController = new FileSystemController(clientConnection, ServerConfig.ROOT_DIR_SERVER);

                UserController userController = new UserController(clientConnection);

                ClientHandler clientHandler = new ClientHandler(clientConnection, fileSystemController, userController);

                executorService.execute(clientHandler);
            }


        } catch(IOException e)
        {
            System.out.println("[Server] Error while creating Socket! Port Already in use...\n" + e.getMessage());
        } finally
        {
            try
            {
                if(serverSocket != null)
                {
                    serverSocket.close(); // Close the server socket

                    System.out.println("[Server] Server socket closed!");
                }

                executorService.shutdown();

            } catch(IOException e)
            {
                System.out.println("[Server] Error while closing server socket: " + e.getMessage());
            }

        }
    }
}
