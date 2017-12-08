import java.io.IOException;
import java.util.*;
import java.lang.StringBuilder;

public class MessageHub
{
    private Vector<ClientHandler> userList;
    private Vector<String> messageList;
    private Integer numOfUsers;
    private Integer userIdentifierCount;
    private String hubID;
        
    public MessageHub(String hubID, ClientHandler creator){
        numOfUsers = new Integer(0);
        userIdentifierCount = new Integer(0);
        this.hubID = hubID;
        // Instantiate User List
        userList = new Vector<ClientHandler>();
        userList.add(creator);
        // Instantiate Message List
        messageList = new Vector<String>();
        System.out.println(hubID + "Created");
    }
    
    /**
     * Handles user joining hub. Returns corresponding user ID.
     * */
    public void userJoin(ClientHandler joining){
        // CRITICAL START
        userList.add(joining);
        numOfUsers++;
        // CRITICAL END
        loadMessages(10, joining);
    }
    /**
     * Handles user leaving hub
     **/
     public boolean userLeft(ClientHandler leaving){
         // CRITICAL START
         userList.remove(leaving);
         numOfUsers--;
         // CRITICAL END
         // Done
         System.out.println(leaving.getUsername() + " hung up from hub: " + hubID);
         broadcast("User #" + leaving.getUsername() + " has left the room");
         return true; 
     } 
    /**
     * Adds message to messageList passing userID and message to form a message
     * of format: "userID: message"
     **/
    public void postMessage(String message){
        // CRITICAL START
        messageList.add(message);
        System.out.println("Message Received in hub ["+ hubID+"] || " + message);
        // CRITICAL END
    }

    /**
     * Check for duplicate username in hub.
     * @return
     */
    public boolean checkUsername(String username){
        for(ClientHandler ch : userList){
            if(ch.getUsername() == username){
                return false; // duplicate username
            }
        }
        return true; // username is fine
    }
    
    /**
     * Sends a message to each client's (in userList) output stream that is in the
     * current hub.
     **/
    private void broadcast(String message){
        // CRITICAL START
        for (ClientHandler ch : userList) {
            ch.dataOS.println(message);
        }
        // CRITICAL END
    }
    
    /**
     * Sends previous messages to client, howFarBack indicates how many previous messages
     * should be sent.
     **/
    private void loadMessages(int howFarBack, ClientHandler client){
        // CRITICAL START
        for(int i = messageList.size() - howFarBack; i < messageList.size(); i++){
            client.dataOS.println(messageList.get(i)); // load stored messages for given hub
        }
        // CRITICAL END
    }
}