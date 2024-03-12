package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class UserInterface
{
    private final FileSystemClient fileSystemClient;

    private final BufferedReader reader;

    private final Socket socket;

    public UserInterface(FileSystemClient fileSystemClient, Socket socket)
    {
        this.socket = socket;

        this.fileSystemClient = fileSystemClient;

        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start()
    {
        try
        {
            socket.setSoTimeout(10000);

        } catch(SocketException e)
        {

            System.out.println("[Client] Server timeout!");
            try
            {
                socket.close();
            } catch(IOException ex)
            {
                System.out.println("[Client] Cannot close the socket! Try again...");
            }
        }
        System.out.println("Welcome to the File System Client!");

        while(socket.isConnected())
        {
            System.out.println("--------------------");
            System.out.println("\t\tMenu");
            System.out.println("--------------------");
            System.out.println("1. List files");
            System.out.println("2. Download file");
            System.out.println("3. Upload file");
            System.out.println("4. Delete file");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try
            {
                int choice = Integer.parseInt(reader.readLine());

                String filePath = "";

                int fileChoice = 0;

                switch(choice)
                {
                    // LIST FILES OF SERVER
                    case 1:
                        fileSystemClient.listFiles();

                        break;

                    // DOWNLOAD FILE FROM SERVER
                    case 2:
                        fileSystemClient.listFiles();

                        System.out.print("Enter your choice: ");

                        fileChoice = Integer.parseInt(reader.readLine());

                        fileSystemClient.reqDownloadFile(fileChoice);

                        break;

                    // UPLOAD FILE TO SERVER
                    case 3:
                        System.out.print("Enter your complete file path: ");

                        filePath = reader.readLine();

                        fileSystemClient.uploadFile(filePath);

                        break;

                    // DELETE FILE FROM SERVER
                    case 4:
                        fileSystemClient.listFiles();

                        System.out.print("Enter your choice: ");

                        fileChoice = Integer.parseInt(reader.readLine());

                        fileSystemClient.deleteFile(fileChoice);
                        break;

                    // EXIT
                    case 0:
                        System.out.println("Exiting client...");

                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }

            } catch(IOException e)
            {
                System.out.println("IOException: " + e.getMessage());

                System.out.println("Server disconnected. Exiting client...");

                break;
            } catch(NumberFormatException numberFormatException)
            {
                System.out.println("NumberFormatException: " + numberFormatException.getMessage());

                System.out.println("Enter valid range = [0-4]");

            }
        }

    }
}
