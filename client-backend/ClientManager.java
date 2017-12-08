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
		new ClientManager().handleUser();
	}
	
	
	public ClientManager() throws IOException {
	// Open the socket and then the writer and reader
		System.out.println("connecting...");
		sock = new Socket("localhost", 1234);
		out = new PrintWriter(sock.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		System.out.println("...connected");
		console = new Scanner(System.in); //get user input to send to server
		this.messageQueue = new ArrayList<String>();
		// Fire off a new thread to handle incoming messages from server
		ServerHandler incoming = new ServerHandler(in);
		incoming.setDaemon(true);
		incoming.start();
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
					System.out.println(line); //Receive messages
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
}
