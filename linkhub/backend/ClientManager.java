package linkhub.backend;

import java.io.*;
import java.util.*;
import java.net.*;
import linkhub.LinkHub;

/**
 * Take note that all reading new messages takes place in a separate thread from
 * the thread which creates the ClientManager. The thread that creates the
 * ClientManager should be a separate thread than the main thread of the application
 * so as to not block the UI/Main thread. In a basic application of this class,
 * one would require three threads; one for sending the actual calls and having the UI on,
 * one for the ClientManager to be created on, and the internal thread created by ClientManager
 * (ServerHandler).
 */
public class ClientManager {
	private Scanner console;
    private Socket sock;							// connection to the server
	private PrintWriter out;						// Send messages to server
	private BufferedReader in;						// Receive messages from server
	private boolean hungup = false;
	private LinkHub gui;
	
	/**
	 * LinkHub object is the frontend class
	 */
	public ClientManager(LinkHub gui){
		try{
	        // Open the socket and then the writer and reader
	        System.out.println("Welcome to Linkhub");
			System.out.print("connecting...");
			sock = new Socket("localhost", 1234);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.print("...connected\n");
			
			this.gui = gui;
			
			// Fire off a new thread to handle incoming messages from server
			ServerHandler incoming = new ServerHandler(in);
			incoming.setDaemon(true);
			incoming.start();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	/**
	 * Create a hub with specified hubID and adds a user with a specified username to the hub.
	 * Returns the response code which is set to return one of three responses:
	 *	-"BLANK": blank field (try to handle this client side if possible)
	 *	-"HUBID TAKEN": a user is trying to create a hub whose name is taken
	 *	-"SUCCESS": the operation the user is trying to preform (create/join) has succeeded
	 *	-"HANGUP": the server hungup
	 */
	public String createHub(String hubID, String username){
		try{
			out.println("Y");
			out.println(username);
			out.println(hubID);
			String response;
			if((response = in.readLine()) != null){
				if(response.equals("SUCCESS")) return null;
				return in.readLine();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			return "HANGUP";
		}
	}
	
	/**
	 * Join a hub with specified hubID and adds a user with a specified username to the hub.
	 * Returns the response code which is set to return one of four responses:
	 *	-"BLANK": blank field (try to handle this client side if possible)
	 *	-"USER TAKEN": a user is trying to join a hub with a username which another user in that hub is using
	 *	-"NOT A HUB": a user is trying to join a hub that doesn't exist (hubID not in system)
	 *	-"SUCCESS": the operation the user is trying to preform (create/join) has succeeded
	 *	-"HANGUP": the server hungup
	 */
	public String joinHub(String hubID, String username){
		try{
			out.println("N");
			out.println(username);
			out.println(hubID);
			String response;
			if((response = in.readLine()) != null){
				if(response.equals("SUCCESS")) return null;
				return in.readLine();
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			return "HANGUP";
		}
	}
	
	/**
	 * Post message to the current hub that the user is assigned to.
	 */ 
	public void postMessage(String message){
		out.println(message);
	}
	
	/**
	 * Leave current hub that the user is assigned to. Send the quit identifier to the server.
	 */
	public void leaveHub(){
		out.println("/q");
	}
	
	/**
	 * Handles communication from the server (via "in"), printing to System.out
	 */
	private class ServerHandler extends Thread {
		private BufferedReader in;

		private ServerHandler(BufferedReader in) {
			this.in = in;
		}

		public void run() {
			String line;
			try {
				while ((line = in.readLine()) != null) {
					// update UI
					gui.receiveMessage(line);
				}
			}
			catch (IOException e) {
				e.printStackTrace();	//whats the error
			}
			finally {
				hungup = true;	//if the server disconnected
				gui.receiveMessage("server hungup");
				System.out.println("server hung up");
			}
		}	
	}


	/**
	 * Get console input and send it to server;
	 * stop & clean up when server has hung up (noted by hungup)
	 */
	public void handleUser() throws IOException {
		while (!hungup) {
			out.println(console.nextLine());
		}
		// Clean up
		out.close();
		in.close();
		sock.close();
	}
}
