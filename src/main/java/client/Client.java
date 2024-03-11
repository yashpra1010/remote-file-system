package client;

import java.io.IOException;
import java.net.Socket;

public class Client
{
    private static final String SERVER_IP = utils.Constants.HOST; // Server IP address

    private static final int PORT = utils.Constants.PORT;

    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket(SERVER_IP, PORT);

            System.out.println("Connected to server: " + SERVER_IP + ":" + PORT);

            ClientRequestHandler requestHandler = new ClientRequestHandler(socket);

            FileSystemClient fileSystemClient = new FileSystemClient(requestHandler);

            UserInterface userInterface = new UserInterface(fileSystemClient);

            userInterface.start();

        } catch(IOException e)
        {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Retrying again in 5 seconds...");
            try
            {
                Thread.sleep(5000);
            } catch(InterruptedException ex)
            {
                System.out.println("Error!\nStatus: FATAL\nMessage: "+ex.getMessage());
            }
            Client.main(null);
        }

    }
}
