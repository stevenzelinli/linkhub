import java.io.IOException;
import java.util.*;
import java.lang.StringBuilder;
import java.util.concurrent.Semaphore;

public class MessageHub
{
    private Vector<ClientHandler> userList;
    private Vector<String> messageList;
    private Integer numOfUsers;
    private Integer userIdentifierCount;
    private String hubID;
    private Semaphore gatekeep, posting;
        
    public MessageHub(String hubID){
        numOfUsers = new Integer(0);
        userIdentifierCount = new Integer(0);
        this.hubID = hubID;
        // Instantiate User List
        userList = new Vector<ClientHandler>();
        // Instantiate Message List
        messageList = new Vector<String>();
        System.out.println(hubID + " Created");
        gatekeep = new Semaphore(1, true);
        posting = new Semaphore(1, true);
    }
    
    /**
     * Handles user joining hub. Returns corresponding user ID.
     * */
    public MessageHub userJoin(ClientHandler joining){
        // CRITICAL START
        try {
            gatekeep.acquire();
            userList.add(joining);
            numOfUsers++;
            gatekeep.release();
        }
        catch(InterruptedException e){
        }
        // CRITICAL END
        //loadMessages(10, joining);
        return this;
    }
    /**
     * Handles user leaving hub
     **/
     public boolean userLeft(ClientHandler leaving){
         // CRITICAL START
         try {
             gatekeep.acquire();
             userList.remove(leaving);
             numOfUsers--;
             gatekeep.release();
         }
         catch(InterruptedException e){
         }
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
        try {
            posting.acquire();
            messageList.add(message);
            System.out.println("Message Received in hub [" + hubID + "] || " + message);
            broadcast(message);
            posting.release();
        }
        catch(InterruptedException e){
        }
        // CRITICAL END
    }

    /**
     * Check for duplicate username in hub.
     * @return
     */
    public boolean checkUsername(String username){
        if(username.equals("")) return false;
        if(userList.isEmpty()) return true;
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
        for (ClientHandler ch : userList) {
            ch.dataOS.println(message);
        }
    }
    
    /**
     * Sends previous messages to client, howFarBack indicates how many previous messages
     * should be sent.
     **/
    public void loadMessages(int howFarBack, ClientHandler client){
        // CRITICAL START
        try {
            if (messageList.size() < howFarBack) {
                if (messageList.isEmpty()) return;
                posting.acquire();
                for (int i = 0; i < messageList.size(); i++) {
                    client.dataOS.println(messageList.get(i)); // load stored messages for given hub
                }
            } else {
                posting.acquire();
                for (int i = messageList.size() - howFarBack; i < messageList.size(); i++) {
                    client.dataOS.println(messageList.get(i)); // load stored messages for given hub
                }
            }
            posting.release();
        }
        catch(InterruptedException e){
        }
        // CRITICAL END
    }
}