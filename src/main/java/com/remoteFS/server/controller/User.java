package com.remoteFS.server.controller;

import com.remoteFS.server.handler.ClientConnection;

import java.util.HashMap;

public class User
{
    static HashMap<String, String> userCredentials = new HashMap<>();

    private final ClientConnection clientConnection;

    public User(ClientConnection clientConnection)
    {
        this.clientConnection = clientConnection;
    }

    public boolean registerUser(String username, String password)
    {
        if(userCredentials.containsKey(username))
        {
            return false;
        }
        else
        {
            userCredentials.put(username, password);

            return true;
        }
    }

    public boolean loginUser(String username, String password)
    {
        if(userCredentials.isEmpty())
        {
            return false;
        }
        if(userCredentials.containsKey(username))
        {
            if(password.equals(userCredentials.get(username)))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
