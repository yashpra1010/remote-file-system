# Remote File System
created by: https://github.com/yashpra1010/
```
remote-file-system/
└─ src/
   └── main/
       └── java/
           ├── server/
           │   ├── Server.java                     // Main class to start the server
           │   ├── ServerConfig.java               // Configuration class for server setup
           │   └── handler/
           │       ├── ClientHandler.java          // Thread/process to handle client requests
           │       └── FileSystemManager.java      // Manages file system operations on the server
           │       └── ClientConnection.java       // Represents a client connection
           │   
           └── client/
               ├── Client.java                     // Main class to start the client
               ├── ClientConfig.java               // Configuration class for client setup
               ├── ui/
               │   └── UserInterface.java          // User interface (command-line)
               └── handler/
                   ├── ServerConnection.java       // Handles client requests and communication
                   └── FileSystemClient.java       // Manages file system operations on the client
```
