import java.util.*;
import java.lang.StringBuilder;
import ClientHandler;

public class MessageHub
{
    private Vector<ClientHandler> userList;
    private Vector<String> messageList;
    private Integer numOfUsers;
    private Integer userIdentifierCount;
        
    public MessageBoard(const String hubID, ClientHandler creator){
        numOfUsers = new Integer(0);
        userIdentifierCount = new Integer(0);
        this.hubID = hubID;
        // Instantiate User List
        userList = new Vector<ClientHandler>();
        userList.add(creator);
        // Instantiate Message List
        messageList = new Vector<String>();
    }
    
    /**
     * Handles user joining hub. Returns corresponding user ID.
     * */
    public int userJoin(ClientHandler joining){
        // CRITICAL START
        userList.add(joining);
        numOfUsers++;
        int temp_id = userIdentifierCount++;
        // CRITICAL END
        loadMessages(10, joining);
        return temp_id;
    }
    /**
     * Handles user leaving hub
     **/
     public boolean userLeft(ClientHandler leaving){
         // CRITICAL START
         userList.remove(leaving);
         numOfUsers--;
         // CRITICAL END
         broadcast("User #" + leaving.userID() + " has left the room");
         return true; 
     } 
    /**
     * Adds message to messageList passing userID and message to form a message
     * of format: "userID: message"
     **/
    public void postMessage(int userID, String message){
        StringBuilder sb = new StringBuilder();
        sb.append(userID);
        sb.append(": ");
        sb.append(message);
        // CRITICAL START
        messageList.add(sb.toString());
        // CRITICAL END
    }
    
    /**
     * Sends a message to each client (in userList) IO stream that is in the
     * current hub.
     **/
    private void broadcast(String message){
        // CRITICAL START
        for (ClientHandler ch : userList){
            ch.dataOS.writeChars(message);
        }
        // CRITICAL END
    }
    
    /**
     * Sends messages to client, howFarBack indicates how many previous messages
     * should be sent.
     **/
    private void loadMessages(int howFarBack, ClientHandler client){
        // CRITICAL START
        for(int i = messageList.size() - howFarBack; i < messageList.size(); i++){
            // load messages
        }
        // CRITICAL END
    }
}