package com.remoteFS.client.ui;

import com.remoteFS.client.handler.FileSystemClient;
import com.remoteFS.client.handler.ServerConnection;
import com.remoteFS.client.handler.UserHandlerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class UserAuthentication
{
    private final UserHandlerClient userHandlerClient;

    private final BufferedReader reader;

    private final Socket socket;

    public UserAuthentication(UserHandlerClient userHandlerClient, Socket socket)
    {
        this.socket = socket;

        this.userHandlerClient = userHandlerClient;

        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start()
    {
        System.out.println("Welcome to the Remote File System!");
        while(socket.isConnected())
        {
            System.out.println("--------------------");
            System.out.println("\t\tMenu");
            System.out.println("--------------------");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try
            {
                var choice = Integer.parseInt(reader.readLine());

                var username = "";
                var password = "";

                switch(choice)
                {
                    // LOGIN
                    case 1:
                        System.out.println("--------------------");
                        System.out.println("\t\tLogin");
                        System.out.println("--------------------");
                        System.out.print("Enter username: ");
                        username = reader.readLine();

                        System.out.print("Enter password: ");
                        password = reader.readLine();

                        if(userHandlerClient.sendLoginReq(username, password))
                        {
                            System.out.println("[Client] Login Successful");

                            var serverConnection = new ServerConnection(socket);

                            var fileSystemClient = new FileSystemClient(serverConnection);

                            var fileManagerUI = new FileManager(fileSystemClient, socket);

                            fileManagerUI.start();

                        }
                        else
                        {
                            System.out.println("[Client] Invalid credentials!");
                        }

                        break;

                    // REGISTER
                    case 2:
                        System.out.println("--------------------");
                        System.out.println("\t\tRegister");
                        System.out.println("--------------------");

                        while(true)
                        {
                            System.out.print("Enter username: ");
                            username = reader.readLine();
                            if(username.length() >= 6)
                            {
                                break;
                            }
                            else
                            {
                                System.out.println("Enter username with more than 6 characters!");
                            }
                        }

                        while(true)
                        {
                            System.out.print("Enter password: ");
                            password = reader.readLine();
                            if(password.length() >= 6)
                            {
                                break;
                            }
                            else
                            {
                                System.out.println("Enter password with more than 6 characters!");
                            }
                        }

                        if(userHandlerClient.sendRegisterReq(username, password))
                        {
                            System.out.println("[Client] Registration successful!");
                        }
                        else
                        {
                            System.out.println("[Client] User Already exists! Registration not successful!");
                        }
                        break;

                    // EXIT
                    case 0:

                        System.out.println("Exiting client...");

                        return;

                    default:

                        System.out.println("Enter valid range = [0-2]");
                }

            } catch(IOException e)
            {
                System.out.println("IOException: " + e.getMessage());

                System.out.println("Server disconnected. Exiting client...");

                break;

            } catch(NumberFormatException numberFormatException)
            {

                System.out.println("Enter valid range = [0-2]");

            }
        }
    }
}
