package com.remoteFS.server;

import com.remoteFS.server.controller.FileSystem;
import com.remoteFS.server.handler.ClientConnection;
import com.remoteFS.server.controller.User;
import com.remoteFS.server.handler.ClientHandler;

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
                var clientSocket = serverSocket.accept();

                var clientConnection = new ClientConnection(clientSocket);

                var fileSystemController = new FileSystem(clientConnection, ServerConfig.ROOT_DIR_SERVER);

                var userController = new User(clientConnection);

                var clientHandler = new ClientHandler(clientConnection, fileSystemController, userController);

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
