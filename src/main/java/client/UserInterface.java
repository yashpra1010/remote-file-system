package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface
{
    private final FileSystemClient fileSystemClient;

    private final BufferedReader reader;

    public UserInterface(FileSystemClient fileSystemClient)
    {
        this.fileSystemClient = fileSystemClient;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start()
    {
        System.out.println("Welcome to the File System Client!");

        while(true)
        {
            System.out.println("--------------------");
            System.out.println("Menu:");
            System.out.println("1. List files");
            System.out.println("2. Download file");
            System.out.println("3. Upload file");
            System.out.println("4. Delete file");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try
            {
                int choice = Integer.parseInt(reader.readLine());

                String fileName = "";

                int fileChoice = 0;

                switch(choice)
                {
                    case 1:
                        //                        System.out.print("Enter your absolute file path: ");
                        //                        filePath = reader.readLine();
                        fileSystemClient.listFiles();
                        break;
                    case 2:
                        fileSystemClient.listFiles();

                        System.out.print("Enter your choice: ");

                        fileChoice = Integer.parseInt(reader.readLine());

                        //                        System.out.print("Enter your file name with extension: ");

                        //                        fileName = reader.readLine();

                        fileSystemClient.downloadFile(fileChoice);
                        //                        fileSystemClient.downloadFile(fileChoice);

                        break;
                    case 3:
                        System.out.print("Enter your file name with extension: ");

                        fileName = reader.readLine();

                        fileSystemClient.uploadFile(fileName);

                        break;
                    case 4:
                        fileSystemClient.listFiles();

                        System.out.print("Enter your choice: ");

                        fileChoice = Integer.parseInt(reader.readLine());

                        fileSystemClient.deleteFile(fileChoice);
                        break;
                    case 0:
                        System.out.println("Exiting client...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch(NumberFormatException | IOException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
