package client;

import java.io.*;
import java.nio.file.*;

public class FileSystemClient
{
    private final ClientRequestHandler requestHandler;

    private static DataOutputStream dataOutputStream = null;

    private static DataInputStream dataInputStream = null;

    public FileSystemClient(ClientRequestHandler requestHandler)
    {
        this.requestHandler = requestHandler;
    }

    public void listFiles()
    {
        try
        {
            String response = requestHandler.sendRequest("LIST");

            System.out.println(response);
        } catch(IOException e)
        {
            System.out.println("[Client] Error listing files from server!");
        }
    }

    public void reqDownloadFile(int fileChoice)
    {
        try
        {
            String response = requestHandler.sendRequest("DOWNLOAD " + fileChoice);

            //            System.out.println(response);

            String command = response.split(" ", 2)[0]; // START_RECEIVING


            if(command.equals("START_RECEIVING"))
            {
                String argument = response.split(" ", 2)[1]; // FILE-NAME

                if(receiveFileFromServer(argument))
                {
                    System.out.println("[Client] File downloaded successfully!");
                }
                else
                {
                    System.out.println("[Client] Error! File not received properly!");
                }
            }
            else
                System.out.println("[Client] File not found on server!");

        } catch(IOException e)
        {
            System.out.println("[Client] Error downloading files from server!");
        }
    }

    public boolean receiveFileFromServer(String fileName)
    {
        requestHandler.writer.println("START_SENDING " + fileName);
        try
        {
            int bytes = 0;

            dataInputStream = new DataInputStream(requestHandler.clientSocket.getInputStream());

            dataOutputStream = new DataOutputStream(requestHandler.clientSocket.getOutputStream());

            FileOutputStream fileOutputStream = new FileOutputStream(ClientConfig.ROOT_DIR_CLIENT + fileName);


            long size = dataInputStream.readLong(); // read file size

            byte[] buffer = new byte[4 * 1024];

            while(size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
            {
                // Here we write the file using write method
                fileOutputStream.write(buffer, 0, bytes);

                size -= bytes; // read upto file size
            }

            fileOutputStream.close();

            return true;

        } catch(IOException e)
        {
            System.out.println("[Client] Error in receiving file from server...\nError: " + e.getMessage());

            return false;
        }
    }


    public boolean uploadFile(String localPath)
    {
        String[] fileDirectories = localPath.split("/");

        String fileName = fileDirectories[fileDirectories.length - 1];


        if(Files.exists(Paths.get(localPath)) && fileName.contains("."))
        {
            try
            {
                requestHandler.writer.println("UPLOAD " + fileName);


                File file = new File(localPath);

                DataOutputStream dataOutputStream = new DataOutputStream(requestHandler.clientSocket.getOutputStream());

                DataInputStream dataInputStream = new DataInputStream(requestHandler.clientSocket.getInputStream());

                FileInputStream fileInputStream = new FileInputStream(file);

                // Here we send the File to Server
                dataOutputStream.writeLong(file.length());

                int bytes = 0;

                // Here we  break file into chunks
                byte[] buffer = new byte[4 * 1024];

                while((bytes = fileInputStream.read(buffer)) != -1)
                {
                    // Send the file to Server Socket
                    dataOutputStream.write(buffer, 0, bytes);

                    dataOutputStream.flush();
                }

                // close the file here
                fileInputStream.close();

                requestHandler.reader.readLine();

                return true;
            } catch(FileNotFoundException e)
            {
                System.out.println("[Client] File not found!");
                return false;
            } catch(IOException io)
            {
                System.out.println("[Client] Data input/output stream error...\nError: " + io.getMessage());
                return false;
            }

        }
        else
        {
            System.out.println("[Client] Incorrect file path!");
            return false;
        }

    }


    public void deleteFile(int fileChoice)
    {
        try
        {
            String response = requestHandler.sendRequest("DELETE " + fileChoice);

            System.out.println(response);

        } catch(IOException e)
        {
            System.out.println("[Client] Error deleting files from server! " + e.getMessage());
        }
    }
}
