import java.io.*;
import java.util.*;
import java.net.*;

public class ClientHandler implements Runnable {
    public BufferedReader dataIS;
    public PrintWriter dataOS;

    private String username;
    private Socket clientSocket;
    private Map<String, MessageHub> chatRooms;
    private MessageHub currentChatRoom;

    public ClientHandler(Socket sock, Map<String, MessageHub> chatRooms) {
        this.clientSocket = sock;
        try {
            dataIS = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            dataOS = new PrintWriter(sock.getOutputStream(), true);
        } catch (IOException e) {
            System.out.print(e.getStackTrace());
        }

        this.chatRooms = chatRooms;
        this.username = "not_set";
    }

    @Override
    public void run() {

        try {
            // verification loop
            while(true) {
                // getting username
                // first message from client will be the client username
                username = dataIS.readLine();
                // second message is the hub id
                String hubID = dataIS.readLine();
                if(chatRooms.containsKey(hubID)){
                    currentChatRoom = chatRooms.get(hubID);
                    if(currentChatRoom.checkUsername(username)){
                        dataOS.println("SUCCESS"); // messages for client to parse
                        break;
                    }
                    else{
                        dataOS.println("NOT UNIQUE"); // messages for client to parse
                    }
                }
                else{
                    dataOS.println("NOT A ROOM"); // messages for client to parse
                }
            }
            // Chat away
            String line;
            while ((line = dataIS.readLine()) != null) {
                String msg = username + ": " + line;
                //TODO
                //  ADD POST() METHOD
                currentChatRoom.postMessage(msg);
            }

            // User Exits
            currentChatRoom.userLeft(this);
            // cleanup
            dataOS.close();
            dataIS.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return this.username;
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
        } else {
            MessageHub newHub = new MessageHub(hubID, this); //create hub
            chatRooms.put(hubID, newHub);     //add it to the Map of hubs
            joinHub(hubID);
            return true;                    //success
        }

    }

}