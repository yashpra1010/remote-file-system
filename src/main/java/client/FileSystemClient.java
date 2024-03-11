package client;

import utils.Constants;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

public class FileSystemClient
{
    private final ClientRequestHandler requestHandler;

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

    public void downloadFile(int fileChoice)
    {
        try
        {
            String response = requestHandler.sendRequest("DOWNLOAD " + fileChoice);
            System.out.println(response);
        } catch(IOException e)
        {
            System.out.println("[Client] Error downloading files from server! " + e.getMessage());
        }
    }

    public void uploadFile(String localPath)
    {
        try
        {
            String response = requestHandler.sendRequest("UPLOAD " + localPath);
            System.out.println(response);
        } catch(IOException e)
        {
            System.out.println("[Client] Error uploading files from server! " + e.getMessage());
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
