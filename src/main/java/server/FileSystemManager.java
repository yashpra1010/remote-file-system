package server;

import java.io.*;
import java.net.Socket;
import java.nio.*;
import java.nio.channels.*;
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
            System.out.println("Error listing files: " + e.getMessage());
        }
        return fileMap;
    }

    public boolean sendFileToClient(Integer fileChoice)
    {
        Path filePath = Paths.get(rootDirectory, fileMap.get(fileChoice));
        System.out.println("Download request for: " + filePath);
        return true;
    }


    public boolean receiveFileFromClient(String filePath)
    {
        System.out.println("Upload request for: " + filePath);
        return true;
    }

    public boolean deleteFile(int fileIndex)
    {
        try
        {
            Path file = Paths.get(rootDirectory, fileMap.get(fileIndex));

            Files.deleteIfExists(file);

            fileMap.remove(fileIndex);

            return true;

        } catch(IOException e)
        {
            System.out.println("Error deleting file: " + e.getMessage());

            return false;
        }
    }
}
