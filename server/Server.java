import java.io.*;
import java.util.*;
import java.net.*;
import ClientHandler.ClientHandler;
import MessageHub.MessageHub;

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
    
    public static void main(String[] args) throws IOException 
    {
        // server is listening on port 1243
        ServerSocket server_socket = new ServerSocket(1234);
         
        Socket socket;
         
        // running infinite loop for getting
        // client request
        while (true) 
        {
            
            // Accept the incoming request
            try
            {
                socket = server_socket.accept();
            }
            catch (IOException e)
            {
                System.out.println("Connection Failed: " + e);
            }
 
            System.out.println("New client request received : " + socket);
             
             
            System.out.println("Creating a new handler for this client...");
            
            BufferedInputStream dataIS = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream dataOS = new BufferedOutputStream(socket.getOutputStream());
            
            Map<String, MessageHub> map = new Map<String, MessageHub>();
            
            // Create a new handler object for handling this request.
            ClientHandler clientHandler = new ClientHandler(socket, map, dataIS, dataOS);
 
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