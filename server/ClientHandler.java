import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.Semaphore;

/**
 * Handles client requests and sends back responses based on certain criteria
 * met. Authenticates client through username, hubid, and creation input from
 * the client. 
 */
public class ClientHandler implements Runnable {
    private BufferedReader dataIS;
    private PrintWriter dataOS;
    private Semaphore creation;
    private String username;
    private Socket clientSocket;
    private Map<String, MessageHub> chatRooms;
    private MessageHub currentChatRoom;
    
    /**
     * Recieves sockets from Server.java instance which assigns sockets and 
     * handles the creation of client handlers. Gets passed a semaphore that
     * will synchronize the creation of MessageHubs and a Map containing all
     * MessageHub objects mapped to their corresponding hubIDs.
     */
    public ClientHandler(Socket sock, Map<String, MessageHub> chatRooms, Semaphore creation) {
        this.clientSocket = sock;
        try {
            dataIS = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            dataOS = new PrintWriter(sock.getOutputStream(), true);
        } 
        catch (IOException e) {
            System.out.print(e.getStackTrace());
        }
        this.creation = creation;
        this.chatRooms = chatRooms;
        this.username = "not_set";
    }

    @Override
    public void run() {
        try {
            // verification will loop until a set of join/create inputs is accepted
            verification();
            // LOAD MESSAGES
            currentChatRoom.loadMessages(10, this);
            // Chatting
            // reading input from client
            String line;
            while ((line = dataIS.readLine()) != null) {
                // if the user decides to quit
                if(line.equals("/q")){
                    // User Exits
                    break;
                }
                String msg = username + ": " + line;
                
                currentChatRoom.postMessage(msg);
            }
            leaveHub();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Prints to the output stream associated with this client.
     */
    public void printToOutput(String message){
        dataOS.println(message);
    }
    
    /**
     * Gets the username associated with the current client handler.
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * Leaves and closes streams and sockets.
     */
    private void leaveHub(){
        // User Exits
        currentChatRoom.userLeft(this);
        try{
            // cleanup
            dataOS.close();
            dataIS.close();
            clientSocket.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Joins a hub using the unique hubID.
     **/
    private MessageHub joinHub(String hubID) {

        if (chatRooms.containsKey(hubID)) {  //returns boolean
            return chatRooms.get(hubID);
        } else {
            return null;
        }
    }

    /**
     * Creates a hub with this as the hub creator
     **/
    private boolean createHub(String hubID) {
        //CRITICAL START
        try {
            creation.acquire();
            if (chatRooms.containsKey(hubID) || hubID.equals("")) {
                creation.release();
                return false;
            } else {
                MessageHub newHub = new MessageHub(hubID, chatRooms); //create hub
                chatRooms.put(hubID, newHub);     //add it to the Map of hubs
                creation.release();
                //currentChatRoom = joinHub(hubID);
                return true;                    //success
            }
        }
        catch(InterruptedException e){
            return false;
        }
        //CRITICAL END
    }
    
    /**
     * Verification loop to verify client inputs for username and hubID based on
     * whether or not they are creating or joining a hub.
     */ 
    private void verification() throws IOException{
        // verification loop
        while(true) {
            // getting username
            // first message from client will be the client username
            String createOrJoin = "L";
            createOrJoin = dataIS.readLine();
            if(!createOrJoin.toUpperCase().equals("Y") && !createOrJoin.toUpperCase().equals("N")){
                continue;
            }

            username = dataIS.readLine();

            // second message is the hub id
            String hubID = dataIS.readLine();

            // check for blank and make a hub if the user decides to
            if(hubID.equals("") || username.equals("")){
                dataOS.println("ERROR: BLANK");
                continue;
            }
            else if(createOrJoin.toUpperCase().equals("Y")){
                if(!createHub(hubID)){
                    dataOS.println("ERROR: HUBID TAKEN");
                    continue;
                }
            }

            if(chatRooms.containsKey(hubID)){
                currentChatRoom = joinHub(hubID);
                if(currentChatRoom.checkUsername(username)){
                    currentChatRoom.userJoin(this);
                    dataOS.println("SUCCESS"); // messages for client to parse
                    break;
                }
                else if(username.equals("")){
                    dataOS.println("ERROR: BLANK");
                }
                else{
                    dataOS.println("ERROR: USER TAKEN"); // messages for client to parse
                }
            }
            else{
                dataOS.println("ERROR: NOT A HUB"); // messages for client to parse
            }
        }
    }
}