import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class Server 
{
    // Vector to store active clients
    static Vector<ClientHandler> activeClients = new Vector<>();
    // Map to store hubs
    static Map<String, MessageHub> messageHubs = new HashMap<String, MessageHub>();
    // counter to keep track of the number of clients
    static int numOfClients = 0;
    static int clientNumber = 0;
    static int port = 1234;
    
    public static void main(String[] args) throws IOException 
    {
        // server is listening on port 1234
        ServerSocket server_socket = new ServerSocket(port);
         
        Socket socket;

        // running infinite loop for getting
        // client request
        while (true) 
        {
            System.out.println("Server Listening on port : " + port);
            // Accept the incoming request
            try
            {
                socket = server_socket.accept();
                System.out.println("New client request received : " + socket);
            }
            catch (IOException e)
            {
                System.out.println("Connection Failed: " + e);
                continue; // drop connection
            }
             
             
            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler clientHandler = new ClientHandler(socket, messageHubs);
 
            // Create a new Thread with this object.
            Thread clientThread = new Thread(clientHandler);
            System.out.println("Adding this client to active client list");
 
            // add this client to active clients list
            activeClients.add(clientHandler);
 
            // start the thread.
            clientThread.start();
        }
    }
}