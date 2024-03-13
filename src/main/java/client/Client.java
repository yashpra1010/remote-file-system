package client;

import client.handler.ServerConnection;
import client.handler.UserHandlerClient;
import client.ui.UserAuthenticationUI;

import java.io.IOException;
import java.net.Socket;

public class Client
{
    private static final String SERVER_IP = ClientConfig.HOST; // Server IP address

    private static final int PORT = ClientConfig.PORT;

    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket(SERVER_IP, PORT);

            System.out.println("[Client] Connected to server: " + SERVER_IP + ":" + PORT);

            ServerConnection serverConnection = new ServerConnection(socket);

            UserHandlerClient userHandlerClient = new UserHandlerClient(serverConnection);

            UserAuthenticationUI userAuthenticationUI = new UserAuthenticationUI(userHandlerClient, socket);

            userAuthenticationUI.start();

        } catch(IOException e)
        {
            System.out.println("[Client] Error: " + e.getMessage());

            System.out.println("[Client] Retrying again in 5 seconds...");
            try
            {
                Thread.sleep(5000);
            } catch(InterruptedException ex)
            {
                System.out.println("[Client] Error!\nStatus: FATAL\nMessage: "+ex.getMessage());
            }
            Client.main(null);
        }
    }
}
