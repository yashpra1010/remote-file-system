package com.remoteFS.server.controller;

import com.remoteFS.server.handler.ClientConnection;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.*;


public class FileSystem
{
    static Map<Integer, String> fileMap = new HashMap<>();

    private final String rootDirectory; // Root directory of the file system

    private final ClientConnection clientConnection;

    public FileSystem(ClientConnection clientConnection, String rootDirectory)
    {
        this.clientConnection = clientConnection;

        this.rootDirectory = rootDirectory;
    }

    public Map<Integer, String> listFiles()
    {
        fileMap.clear();

        try
        {
            var directory = Paths.get(rootDirectory);

            var counter = new AtomicInteger(0);

            Files.list(directory).forEach(path -> fileMap.put(counter.incrementAndGet(), path.getFileName().toString()));

        } catch(NullPointerException npe)
        {
            System.out.println("[Server] Server is down!");
        }
        catch(IOException e)
        {
            System.out.println("[Server] Error listing files: " + e.getMessage());
        }

        return fileMap;
    }

    public String sendFileName(Integer fileChoice)
    {
        if(fileMap.containsKey(fileChoice))
        {
            return "START_RECEIVING " + fileMap.get(fileChoice);
        }
        else
        {
            return "FILE_NOT_FOUND";
        }
    }

    public boolean sendFileToClient(String fileName)
    {
        var file = new File(rootDirectory + fileName);

        try
        {
            var dataOutputStream = new DataOutputStream(clientConnection.clientSocket.getOutputStream());

            var fileInputStream = new FileInputStream(file);

            // Here we send the File to Client
            dataOutputStream.writeLong(file.length());

            var bytes = 0;

            // Here we  break file into 8KB chunks
            var buffer = new byte[8192];

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

        } catch(NullPointerException npe)
        {
            System.out.println("[Server] Server is down!");

            return false;
        }
        catch(IOException io)
        {
            System.out.println("[Server] Data input/output stream error...\nError: " + io.getMessage());

            return false;

        }
    }

    public boolean startReceivingFileFromClient(String fileName)
    {
        try
        {
            var bytes = 0;

            var dataInputStream = new DataInputStream(clientConnection.clientSocket.getInputStream());

            var fileOutputStream = new FileOutputStream(rootDirectory + fileName);

            var size = dataInputStream.readLong(); // read file size

            var buffer = new byte[8192]; // 8KB

            while(size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1)
            {
                // Here we write the file using write method
                fileOutputStream.write(buffer, 0, bytes);

                size -= bytes; // read upto file size
            }

            fileOutputStream.close();

            return true;

        } catch(NullPointerException npe)
        {
            System.out.println("[Server] Server is down!");

            return false;
        }catch(IOException e)
        {
            System.out.println("[Server] Error in receiving file from server...\nError: " + e.getMessage());

            return false;
        }
    }

    public boolean deleteFile(int fileIndex)
    {
        try
        {
            if(fileMap.containsKey(fileIndex))
            {
                var file = Paths.get(rootDirectory, fileMap.get(fileIndex));

                Files.deleteIfExists(file);

                fileMap.remove(fileIndex);

                return true;
            }
            else
            {
                return false;
            }

        } catch(NullPointerException npe)
        {
            System.out.println("[Server] Server is down!");

            return false;
        }catch(IOException e)
        {
            System.out.println("[Server] Error deleting file: " + e.getMessage());

            return false;
        }
    }
}
