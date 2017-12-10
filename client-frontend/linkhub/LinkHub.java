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

public class LinkHub extends JFrame {

  public static void main(String args[]) {
    new LinkHub();
  }

  private JPanel cardPanel;
  private String username;
  private String hubID;
  private Hub hub;

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

  public void loginUser(Hub newHub, String username, String hubID) {
    this.hub = newHub;
    this.username = username;
    this.hubID = hubID;
  }

  public void createHub(Hub newHub, String username, String hubID) {
    this.hub = newHub;
    this.username = username;
    this.hubID = hubID;
  }

  public String getHubID() {
    return this.hubID;
  }

  public String getUsername() {
    return this.username;
  }
  
  public void sendMessage(String message) {
    
  }
  
  public void receiveMessage(String message) {
    this.hub.addMessage(message);
  }
  
  public void exitHub() {
    this.username = null;
    this.hubID = null;
    this.hub = null;
  }

}
