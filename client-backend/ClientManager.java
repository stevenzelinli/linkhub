import java.io.*;
import java.util.*;
import java.net.*;

public class ClientManager {
	private Scanner console;
    private Socket sock;							// connection to the server
	private PrintWriter out;						// Send messages to server
	private BufferedReader in;						// Receive messages from server
	private boolean hungup = false;
	private ArrayList<String> messageQueue;
	
	public static void main(String[] args) throws IOException {
		new ClientManager();
	}
	
	
	public ClientManager() throws IOException {
        // Open the socket and then the writer and reader
        System.out.println("Welcome to Linkhub");
		System.out.print("connecting...");
		sock = new Socket("localhost", 1234);
		out = new PrintWriter(sock.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		System.out.print("...connected\n");

		console = new Scanner(System.in); //get user input to send to server
		this.messageQueue = new ArrayList<String>();
		// Authenticate User
        authenticate();
		// Fire off a new thread to handle incoming messages from server
		ServerHandler incoming = new ServerHandler(in);
		incoming.setDaemon(true);
		incoming.start();

        handleUser();
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
					System.out.println(line); //Receive messages and print to console
				}
			}
			catch (IOException e) {
				e.printStackTrace();	//whats the error
			}
			finally {
				hungup = true;	//if the server disconnected
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

    /**
     * Authenticates the username and hubID to join.
     * @throws IOException
     */
	private void authenticate() throws IOException{
	    boolean authenticated = false;
	    while(!authenticated) {
	        System.out.print("Do you want to create a hub? (Y/N): ");
	        String createOrJoin = console.nextLine();
            if(!createOrJoin.toUpperCase().equals("Y") && !createOrJoin.toUpperCase().equals("N")) continue;
	        out.println(createOrJoin);
            // get username
            System.out.print("Username: ");
            out.println(console.nextLine());

            // get password
            System.out.print("Hub ID: ");
            out.println(console.nextLine());

            // server response
            try {
                String response = in.readLine();
                if(response.equals("SUCCESS")){
                    authenticated = true;
                    System.out.println("ACCESSING HUB");
                }
                else if(response.equals("NOT UNIQUE")){
                    System.out.println("USERNAME IN USE");
                }
                else if(response.equals("NOT A HUB")){
                    System.out.println("HUB DOES NOT EXIST");
                }
                else if(response.equals("BLANK")){
                    System.out.println("BLANK FIELDS");
                }
                else if(response.equals("HUBID TAKEN")){
                    System.out.println("HUB ID ALREADY EXISTS");
                }
                else if(response.equals("USER TAKEN")){
                    System.out.println("USERNAME TAKEN IN HUB");
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
