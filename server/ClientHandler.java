import java.io.*;
import java.util.*;
import java.net.*;

public class ClientHandler implements Runnable {
    public BufferedInputStream dataIS;
    public BufferedOutputStream dataOS;

    private int userID;
    private Socket clientSocket;
    private Map<String, MessageHub> chatRooms;
    private String currentChatRoom;

    public ClientHandler(Socket sock, Map<String, MessageHub> chatRooms, BufferedInputStream in, BufferedOutputStream out) {
        this.clientSocket = sock;
        this.dataIS = in;
        this.dataOS = out;
        this.chatRooms = chatRooms;
        this.userID = 0;
    }

    @Override
    public void run() {

//        try {
//            // Chat away
//            String line;
//            while ((line = dataIS.read()) != null) {
//                String msg = "Message: " + line;
//                System.out.println(msg);
//                //TODO
//                //  ADD POST() METHOD
//                //	server.broadcast(this, msg);
//            }
//
//
//        }
//        catch (IOException e){
//
//        }
    }

    public int userID() {
        return this.userID;
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
        if (chatRooms.containsKey(hubID)) {
            return false;
        }
        
        else{
            MessageHub newHub = new MessageHub(hubID, this); //create hub
            chatRooms.put(hubID, newHub);     //add it to the Map of hubs
            joinHub(hubID);
            return true;                    //success
        }

    }

}