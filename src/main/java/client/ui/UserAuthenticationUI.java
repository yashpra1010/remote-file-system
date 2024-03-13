package client.ui;

import client.handler.FileSystemClient;
import client.handler.ServerConnection;
import client.handler.UserHandlerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class UserAuthenticationUI
{
    private final UserHandlerClient userHandlerClient;

    private final BufferedReader reader;

    private final Socket socket;

    public UserAuthenticationUI(UserHandlerClient userHandlerClient, Socket socket)
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
                int choice = Integer.parseInt(reader.readLine());

                String username = "";
                String password = "";

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

                        boolean login = userHandlerClient.sendLoginReq(username, password);

                        if(login)
                        {
                            System.out.println("[Client] Login Successful");

                            ServerConnection serverConnection = new ServerConnection(socket);

                            FileSystemClient fileSystemClient = new FileSystemClient(serverConnection);

                            FileManagerUI fileManagerUI = new FileManagerUI(fileSystemClient, socket);

                            fileManagerUI.start();

                            break;
                        }
                        else
                        {
                            System.out.println("[Client] Invalid credentials!");
                            break;
                        }

                    // REGISTER
                    case 2:
                        System.out.println("--------------------");
                        System.out.println("\t\tRegister");
                        System.out.println("--------------------");
                        System.out.print("Enter username: ");
                        username = reader.readLine();
                        System.out.print("Enter password: ");
                        password = reader.readLine();

                        boolean register = userHandlerClient.sendRegisterReq(username, password);

                        if(register)
                        {
                            System.out.println("[Client] Registration successful!");
                            break;
                        }
                        else
                        {
                            System.out.println("[Client] User Already exists! Registration not successful!");
                            break;
                        }

                    // EXIT
                    case 0:

                        System.out.println("Exiting client...");

                        return;

                    default:

                        System.out.println("Invalid choice. Please try again.");
                }

            } catch(IOException e)
            {
                System.out.println("IOException: " + e.getMessage());

                System.out.println("Server disconnected. Exiting client...");

                break;

            } catch(NumberFormatException numberFormatException)
            {
                System.out.println("NumberFormatException: " + numberFormatException.getMessage());

                System.out.println("Enter valid range = [0-2]");

            }
        }
    }
}
