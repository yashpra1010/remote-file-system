package com.remoteFS.client.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection
{
    protected final Socket clientSocket;

    protected final BufferedReader reader;

    protected final PrintWriter writer;


    public ServerConnection(Socket clientSocket) throws IOException
    {
        this.clientSocket = clientSocket;

        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public String sendRequest(String request) throws IOException
    {
        writer.println(request); // Send request to server

        return reader.readLine(); // Receive response from server
    }

}
