import java.io.IOException;
import java.util.*;
import java.lang.StringBuilder;
import java.util.concurrent.Semaphore;

/**
 * Stores all messages and broadcasts to all clients for each message sent.
 * Has a circular dependency with ClientHandler.
 */
public class MessageHub
{
    private Map<String, MessageHub> chatRooms;
    private Vector<ClientHandler> userList;
    private Vector<String> messageList;
    private Integer numOfUsers;
    private Integer userIdentifierCount;
    private String hubID;
    /**
     * The gatekeep semaphore handles users leaving and entering the chatRoom
     */
    private Semaphore gatekeep, posting;
    
    /**
     * Gets passed a unique hubID and a map of all chat rooms (for self removal)
     */
    public MessageHub(String hubID, Map<String, MessageHub> chatRooms){
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
        this.chatRooms = chatRooms;
    }
    
    /**
     * Handles user joining hub.
     * */
    public MessageHub userJoin(ClientHandler joining){
        try {
            gatekeep.acquire();
            userList.add(joining);
            numOfUsers++;
            gatekeep.release();
        }
        catch(InterruptedException e){
        }
        return this;
    }
    
    /**
     * Handles user leaving hub. Removes them from the userList and terminates their session.
     **/
    public boolean userLeft(ClientHandler leaving){
        try {
            gatekeep.acquire();
            userList.remove(leaving);
            numOfUsers--;
            // If there are no users after the user leaves then the room is "destroyed"
            if(numOfUsers == 0){
                chatRooms.remove(hubID);
                System.out.println("NO USERS LEFT IN CHATROOM ["+hubID+"], HUB DESTROYED");
                gatekeep.release();
                return true;
            }
            System.out.println(leaving.getUsername() + " hung up from hub: " + hubID);
            broadcast("User " + leaving.getUsername() + " has left the room");
            gatekeep.release();
            return true; 
        }
        catch(InterruptedException e){
            return false;
        }
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
            ch.printToOutput(message);
        }
    }
    
    /**
     * Sends previous messages to client, howFarBack indicates how many previous messages
     * should be sent.
     **/
    public void loadMessages(int howFarBack, ClientHandler client){
        try {
            if (messageList.size() < howFarBack) {
                if (messageList.isEmpty()) return;
                posting.acquire();
                for (int i = 0; i < messageList.size(); i++) {
                    client.printToOutput(messageList.get(i)); // load stored messages for given hub
                }
            } 
            else {
                posting.acquire();
                for (int i = messageList.size() - howFarBack; i < messageList.size(); i++) {
                    client.printToOutput(messageList.get(i)); // load stored messages for given hub
                }
            }
            posting.release();
        }
        catch(InterruptedException e){
        }
    }
}