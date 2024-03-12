package server;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;


public class FileSystemManager
{
    static Map<Integer, String> fileMap = new HashMap<>();

    private final String rootDirectory; // Root directory of the file system

    private final ClientConnection clientConnection;

    public FileSystemManager(ClientConnection clientConnection, String rootDirectory)
    {
        this.clientConnection = clientConnection;
        this.rootDirectory = rootDirectory;
    }

    public Map<Integer, String> listFiles()
    {
        try
        {
            Path directory = Paths.get(rootDirectory);

            //            System.out.println(directory);

            AtomicInteger counter = new AtomicInteger(0);

            Files.list(directory).forEach(path -> {
                fileMap.put(counter.incrementAndGet(), path.getFileName().toString());
            });

        } catch(IOException e)
        {
            System.out.println("[Server] Error listing files: " + e.getMessage());
        }
        return fileMap;
    }

    public String sendFileName(Integer fileChoice)
    {
        if(fileMap.containsKey(fileChoice))
            return "START_RECEIVING " + fileMap.get(fileChoice);
        else
            return "FILE_NOT_FOUND";
    }

    public boolean sendFileToClient(String fileName)
    {
        File file = new File(rootDirectory + fileName);

        try
        {
            DataOutputStream dataOutputStream = new DataOutputStream(clientConnection.clientSocket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(clientConnection.clientSocket.getInputStream());
            FileInputStream fileInputStream = new FileInputStream(file);
            // Here we send the File to Client
            dataOutputStream.writeLong(file.length());

            int bytes = 0;

            // Here we  break file into chunks
            byte[] buffer = new byte[4 * 1024];

            while((bytes = fileInputStream.read(buffer)) != -1)
            {
                // Send the file to Client Socket
                dataOutputStream.write(buffer, 0, bytes);

                dataOutputStream.flush();
            }

            // close the file here
            fileInputStream.close();

            return true;
        } catch(FileNotFoundException e)
        {
            System.out.println("[Server] File not found!");
            return false;
        } catch(IOException io)
        {
            System.out.println("[Server] Data input/output stream error...\nError: " + io.getMessage());
            return false;
        }
    }


    public boolean receiveFileFromClient(String fileName)
    {
        return false;
    }

    public boolean deleteFile(int fileIndex)
    {
        try
        {
            if(fileMap.containsKey(fileIndex))
            {
                Path file = Paths.get(rootDirectory, fileMap.get(fileIndex));

                Files.deleteIfExists(file);

                fileMap.remove(fileIndex);

                return true;
            }
            else {
                return false;
            }

        } catch(IOException e)
        {
            System.out.println("[Server] Error deleting file: " + e.getMessage());

            return false;
        }
    }
}
