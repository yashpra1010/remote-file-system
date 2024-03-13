package client.ui;

import client.handler.FileSystemClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class FileManagerUI
{
    private final FileSystemClient fileSystemClient;

    private final BufferedReader reader;

    private final Socket socket;

    public FileManagerUI(FileSystemClient fileSystemClient, Socket socket)
    {
        this.socket = socket;

        this.fileSystemClient = fileSystemClient;

        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start()
    {
        System.out.println("Welcome to the Remote File System!");

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

                        System.out.print("Enter your choice (0) to exit: ");

                        fileChoice = Integer.parseInt(reader.readLine());

                        if(fileChoice==0)
                        {
                            break;
                        }
                        else
                        {
                            fileSystemClient.reqDownloadFile(fileChoice);
                        }

                        break;

                    // UPLOAD FILE TO SERVER
                    case 3:

                        System.out.print("Enter your complete file path or (0) to exit: ");

                        filePath = reader.readLine();

                        if(filePath.equals("0"))
                        {
                            break;
                        }
                        else
                        {
                            fileSystemClient.uploadFile(filePath);
                        }


                        break;

                    // DELETE FILE FROM SERVER
                    case 4:

                        fileSystemClient.listFiles();

                        System.out.print("Enter your choice (0) to exit: ");

                        fileChoice = Integer.parseInt(reader.readLine());

                        if(fileChoice==0)
                        {
                            break;
                        }
                        else
                        {
                            fileSystemClient.deleteFile(fileChoice);
                        }
                        break;

                    // EXIT
                    case 0:

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
