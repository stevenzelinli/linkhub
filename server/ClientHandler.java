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
                System.out.println("USER ENTERED NAME");
                // second message is the hub id
                String hubID = dataIS.readLine();
                System.out.println("USER ENTERED HUB ID");
                if(chatRooms.containsKey(hubID)){
                    currentChatRoom = chatRooms.get(hubID);
                    if(currentChatRoom.checkUsername(username)){
                        System.out.println("SUCCESS");
                        dataOS.println("SUCCESS"); // messages for client to parse
                        break;
                    }
                    else{
                        System.out.println("NOT UNIQUE");
                        dataOS.println("NOT UNIQUE"); // messages for client to parse
                    }
                }
                else{
                    System.out.println("NOT A HUB");
                    dataOS.println("NOT A HUB"); // messages for client to parse
                }
            }
            // Chat away
            String line;
            while ((line = dataIS.readLine()) != null) {
                String msg = username + ": " + line;
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