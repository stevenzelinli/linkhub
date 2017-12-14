package linkhub.panels;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import java.util.Vector;


import linkhub.LinkHub;

public class Hub extends JPanel {

  public class SendButtonListener  implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      LinkHub frame = (LinkHub) javax.swing.FocusManager.getCurrentManager().getActiveWindow();
      
      String message = username + ": " + Hub.this.messageTextbox.getText() + "\n";
      textArea.append(message);
      frame.sendMessage(message);
      messageTextbox.setText("");
    }
  }

  public class ExitButtonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      LinkHub frame = (LinkHub) javax.swing.FocusManager.getCurrentManager().getActiveWindow();
      
      frame.sendMessage("***" + username + " has left the hub.");
      frame.exitHub();
      
      JPanel cards = frame.getCards();
      CardLayout cardLayout = (CardLayout) cards.getLayout();
      cardLayout.show(cards, "Home");
    }
  }

  JTextField messageTextbox;
  JTextArea textArea;
  String username;

  public Hub(String username, String hubID, Dimension d) {
    this.username = username;
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    this.setLayout(gridbag);

    // Create new entry panel
    JPanel messagesPanel = new JPanel();
    messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
    messagesPanel.setBackground(Color.black);

    //Create input pane
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(Color.black);
    titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

    ImageIcon logoIcon = new ImageIcon(this.getClass().getResource("../assets/logo.jpeg"));
    Image image = logoIcon.getImage();
    Image newImg = image.getScaledInstance(94, 30, java.awt.Image.SCALE_SMOOTH);
    logoIcon = new ImageIcon(newImg);

    JLabel logo = new JLabel(logoIcon);
    titlePanel.add(logo);
    titlePanel.add(Box.createHorizontalGlue());

    // Create title
    JLabel title = new JLabel(hubID);
    title.setBackground(Color.black);
    title.setForeground(Color.orange);
    title.setOpaque(true);
    titlePanel.add(title);


    titlePanel.add(Box.createHorizontalGlue());
    JButton exitButton = new JButton("Exit");
    exitButton.addActionListener(new ExitButtonListener());
    titlePanel.add(exitButton);

    messagesPanel.add(titlePanel);

    textArea = new JTextArea();
    
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    messagesPanel.add(Box.createRigidArea(new Dimension(0,5)));
    messagesPanel.add(scrollPane);
    messagesPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

    //Create input pane
    JPanel inputPanel = new JPanel();
    inputPanel.setBackground(Color.black);
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

    // Create message textbox
    messageTextbox = new JTextField();
    messageTextbox.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
    inputPanel.add(messageTextbox);

    inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
    // Create message textbox
    JButton sendButton = new JButton("Send");
    sendButton.addActionListener(new SendButtonListener());
    inputPanel.add(sendButton);

    messagesPanel.add(inputPanel);

    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1.0;
    c.weighty = 1.0;
    c.fill = GridBagConstraints.BOTH;
    this.add(messagesPanel, c);

  }
  
  public void addMessage(String message) {
    messageTextbox.setText(messageTextbox.getText() + message);
  }

}
