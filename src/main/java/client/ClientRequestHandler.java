package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestHandler
{
    protected final Socket clientSocket;

    protected final BufferedReader reader;

    protected final PrintWriter writer;


    public ClientRequestHandler(Socket clientSocket) throws IOException
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

    public void close() throws IOException
    {
        reader.close(); // Close input stream

        writer.close(); // Close output stream

        clientSocket.close(); // Close socket connection
    }

}
