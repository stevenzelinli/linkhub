package linkhub;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;

import linkhub.panels.*;
import linkhub.backend.*;

public class LinkHub extends JFrame {

  public static void main(String args[]) {
    new LinkHub();
  }

  private JPanel cardPanel;
  private String username;
  private String hubID;
  private Hub hub;
  private ClientManager clientManager;

  LinkHub() {

    cardPanel = new JPanel(new CardLayout());

    Dimension d = new Dimension();
    d.width = 500;
    d.height = 500;

    Home homePanel = new Home(d);

    cardPanel.add(homePanel, "Home");

    this.add(cardPanel);
    this.setSize(500,500);
    this.setTitle("LinkHub");
    this.setVisible(true);

  }

  public JPanel getCards() {
    return cardPanel;
  }

  public String joinHub(Hub newHub, String username, String hubID) {
    clientManager = new ClientManager(LinkHub.this);
    String error = clientManager.joinHub(hubID, username);
    
    if(error != null) {
      return error;
    }
    
    this.hub = newHub;
    this.username = username;
    this.hubID = hubID;
    
    return null;
  }

  public String createHub(Hub newHub, String username, String hubID) {
    clientManager = new ClientManager(LinkHub.this);
    String error = clientManager.createHub(hubID, username);
    
    if(error != null) {
      return error;
    }
    
    this.hub = newHub;
    this.username = username;
    this.hubID = hubID;
    
    return null;
  }

  public String getHubID() {
    return this.hubID;
  }

  public String getUsername() {
    return this.username;
  }
  
  public void sendMessage(String message) {
    clientManager.postMessage(message);
  }
  
  public void receiveMessage(String message) {
    System.out.print(message);
    //this.hub.addMessage(message);
  }
  
  public void exitHub() {
    this.username = null;
    this.hubID = null;
    this.hub = null;
    clientManager.leaveHub();
  }

}
