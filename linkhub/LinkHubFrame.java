package linkhub;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;


import linkhub.panels.*;
import linkhub.backend.*;

public class LinkHubFrame extends JFrame implements Observer {

  private JPanel cardPanel;
  private String username;
  private String hubID;
  public Hub hub;
  public Home homePanel;
  
  public String server = "localhost";
  public int port = 1234;
  
  private LinkHubAccess linkHubAccess;
    
  public static void main(String args[]) {
    LinkHubAccess access = new LinkHubAccess();
    LinkHubFrame frame = new LinkHubFrame(access);
  }

  LinkHubFrame(LinkHubAccess access) {
    this.linkHubAccess = access;
    linkHubAccess.addObserver(this);
    
    cardPanel = new JPanel(new CardLayout());

    Dimension d = new Dimension();
    d.width = 500;
    d.height = 500;

    homePanel = new Home(d);

    cardPanel.add(homePanel, "Home");

    this.add(cardPanel);
    this.setSize(500,500);
    this.setTitle("LinkHub");
    this.setVisible(true);

  }

  public JPanel getCards() {
    return cardPanel;
  }

  public void joinHub(String username, String hubID) {
    try {
        this.linkHubAccess.InitSocket(server, port);
    } catch (IOException ex) {
        System.out.println("Cannot connect to " + server + ":" + port);
        ex.printStackTrace();
        System.exit(0);
    }
    
    this.linkHubAccess.joinHub(hubID, username);
    
    this.username = username;
    this.hubID = hubID;
  }

  public void createHub(String username, String hubID) {
    try {
        this.linkHubAccess.InitSocket(server, port);
    } catch (IOException ex) {
        System.out.println("Cannot connect to " + server + ":" + port);
        ex.printStackTrace();
        System.exit(0);
    }
    
    this.linkHubAccess.createHub(hubID, username);
    
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
    this.linkHubAccess.send(message);
  }
  
  public void exitHub() {
    this.username = null;
    this.hubID = null;
    this.hub = null;
    this.sendMessage("/q");
  }
  
  @Override
  public void update(Observable o, Object arg) {
      final Object finalArg = arg;
      if(finalArg.toString().split(":")[0].equals("ERROR")) {
        JOptionPane.showMessageDialog(homePanel, finalArg.toString());
      } else if(finalArg.toString().equals("SUCCESS")) {
        Dimension d = new Dimension();
        d.width = 500;
        d.height = 500;
        
        this.hub = new Hub(this.username, this.hubID, d);
        cardPanel.add(this.hub, "Hub");
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, "Hub");
      } else {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              if(LinkHubFrame.this.hub != null) {
                LinkHubFrame.this.hub.addMessage(finalArg.toString() + "\n");
              }
            }
        });
      }
  }
}
