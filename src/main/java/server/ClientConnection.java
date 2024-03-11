package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection
{
    protected final Socket clientSocket;

    private final BufferedReader reader;

    private final PrintWriter writer;

    public ClientConnection(Socket clientSocket) throws IOException
    {
        this.clientSocket = clientSocket;

        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);

    }

    public String receive() throws IOException
    {
        return reader.readLine(); // Receive data from the client
    }

    public void send(String data)
    {
        writer.println(data); // Send data to the client
    }

    public void close() throws IOException
    {
        reader.close(); // Close input stream
        writer.close(); // Close output stream
        clientSocket.close(); // Close socket connection
    }
}
