package client.handler;

import client.ClientConfig;
import java.io.*;
import java.nio.file.*;

public class FileSystemClient
{
    private final ServerConnection serverConnection;

    public FileSystemClient(ServerConnection serverConnection)
    {
        this.serverConnection = serverConnection;
    }

    public void listFiles()
    {
        try
        {
            String response = serverConnection.sendRequest("LIST");

            System.out.println(response);

        } catch(IOException e)
        {
            System.out.println("[Client] Server timeout or Error listing files from server!");
        }
    }

    public void reqDownloadFile(int fileChoice)
    {
        try
        {
            String response = serverConnection.sendRequest("DOWNLOAD " + fileChoice);

            String command = response.split(" ", 2)[0]; // "START_RECEIVING" command

            if(command.equals("START_RECEIVING"))
            {
                String argument = response.split(" ", 2)[1]; // FILE-NAME

                if(receiveFileFromServer(argument))
                {
                    System.out.println("[Client] File downloaded successfully!");
                    
                    serverConnection.reader.readLine();
                
                }
                else
                {
                    System.out.println("[Client] Error! File not received properly!");
                }
            }
            else
            {
                System.out.println("[Client] File not found on server!");
            }

        } catch(IOException e)
        {
            System.out.println("[Client] Error downloading files from server!");
        }
    }

    public boolean receiveFileFromServer(String fileName)
    {
        serverConnection.writer.println("START_SENDING " + fileName);
        
        try
        {
            int bytes = 0;

            DataInputStream dataInputStream = new DataInputStream(serverConnection.clientSocket.getInputStream());

            FileOutputStream fileOutputStream = new FileOutputStream(ClientConfig.ROOT_DIR_CLIENT + fileName);

            // read file size
            long size = dataInputStream.readLong();

            byte[] buffer = new byte[8192]; // 8KB

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
                serverConnection.writer.println("UPLOAD " + fileName);

                File file = new File(localPath);

                DataOutputStream dataOutputStream = new DataOutputStream(serverConnection.clientSocket.getOutputStream());

                FileInputStream fileInputStream = new FileInputStream(file);

                // Here we send the File to Server
                dataOutputStream.writeLong(file.length());

                int bytes = 0;

                // Here we break file into 8KB chunks
                byte[] buffer = new byte[8192];

                while((bytes = fileInputStream.read(buffer)) != -1)
                {
                    // Send the file to Server Socket
                    dataOutputStream.write(buffer, 0, bytes);

                    dataOutputStream.flush();
                }

                // close the file here
                fileInputStream.close();

                serverConnection.reader.readLine();

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
            String response = serverConnection.sendRequest("DELETE " + fileChoice);

            System.out.println(response);

        } catch(IOException e)
        {
            System.out.println("[Client] Error deleting files from server! " + e.getMessage());
        }
    }
}
