package linkhub.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class LinkHubAccess extends Observable {
        private Socket socket;
        private OutputStream outputStream;
    
        /** Notify observers of thread event */
        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        /** Create socket, and receiving thread */
        public void InitSocket(String server, int port) throws IOException {
            socket = new Socket(server, port);
            outputStream = socket.getOutputStream();

            Thread receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null)
                            notifyObservers(line);
                    } catch (IOException ex) {
                        notifyObservers(ex);
                    }
                }
            };
            receivingThread.start();
        }

        private static final String CRLF = "\n"; // newline

        /** Create a new hub */
        public void createHub(String hubID, String username) {
    	    try{
    			outputStream.write(("Y" + CRLF).getBytes());
    			outputStream.write((username + CRLF).getBytes());
    			outputStream.write((hubID + CRLF).getBytes());
                outputStream.flush();
    		} catch (IOException ex) {
                notifyObservers(ex);
            }
	    }
        
        /** Join an existing hub */
        public void joinHub(String hubID, String username) {
    	    try{
    			outputStream.write(("N" + CRLF).getBytes());
    			outputStream.write((username + CRLF).getBytes());
    			outputStream.write((hubID + CRLF).getBytes());
                outputStream.flush();
    		} catch (IOException ex) {
                notifyObservers(ex);
            }
	    }
        
        /** Send a message */
        public void send(String text) {
            try {
                outputStream.write((text + CRLF).getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }

        /** Close the socket */
        public void close() {
            try {
                socket.close();
            } catch (IOException ex) {
                notifyObservers(ex);
            }
        }
    }