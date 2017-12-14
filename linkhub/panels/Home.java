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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import linkhub.LinkHub;

public class Home extends JPanel {

  public class JoinButtonListener  implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
        LinkHub frame = (LinkHub) javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        JPanel cards = frame.getCards();
        
        String username = Home.this.usernameTextbox.getText();
        String hubID = Home.this.hubID.getText();
        
        Dimension d = new Dimension();
        d.width = 500;
        d.height = 500;
        Hub hubPanel = new Hub(username, hubID, d);
        cards.add(hubPanel, "Hub");

        String error = frame.joinHub(hubPanel, username, hubID);
        
        if(error != null) {
          JOptionPane.showMessageDialog(Home.this,error);
        } else {
          CardLayout cardLayout = (CardLayout) cards.getLayout();
          cardLayout.show(cards, "Hub");
        }
    }
  }

  public class CreateButtonListener  implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
        LinkHub frame = (LinkHub) javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        JPanel cards = frame.getCards();
        
        String username = Home.this.usernameTextbox.getText();
        String hubID = Home.this.hubID.getText();
        
        Dimension d = new Dimension();
        d.width = 500;
        d.height = 500;
        Hub hubPanel = new Hub(username, hubID, d);
        cards.add(hubPanel, "Hub");

        String error = frame.createHub(hubPanel, username, hubID);
        
        if(error != null) {
          JOptionPane.showMessageDialog(Home.this,error);
        } else {
          CardLayout cardLayout = (CardLayout) cards.getLayout();
          cardLayout.show(cards, "Hub");
        }
    }
  }

  public JTextField usernameTextbox;
  public JTextField hubID;

  public Home(Dimension d) {
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    this.setLayout(gridbag);

    this.setBackground(Color.black);

    c.fill = GridBagConstraints.HORIZONTAL;

    c.weightx = 1.0;
    c.weighty = 1.0;

    c.anchor = GridBagConstraints.CENTER;

    c.insets.top = 5;
    c.insets.bottom = 5;
    c.insets.left = 5;
    c.insets.right = 5;

    c.gridx = 0;
    c.gridy = 0;
    c.gridheight = 1;
    c.gridwidth = 5;

    ImageIcon logoIcon = new ImageIcon(this.getClass().getResource("../assets/logo.jpeg"));
    Image image = logoIcon.getImage();
    Image newImg = image.getScaledInstance(376, 120, java.awt.Image.SCALE_SMOOTH);
    logoIcon = new ImageIcon(newImg);

    JLabel label = new JLabel(logoIcon);
    gridbag.setConstraints(label, c);
    this.add(label);

    // Create new entry panel
    JPanel entryPanel = new JPanel(new GridBagLayout());
    entryPanel.setBackground(Color.black);

    // Create username textbox
    c.gridx = 0;
    c.gridy = 1;
    c.gridheight = 1;
    c.gridwidth = 2;

    usernameTextbox = new JTextField("Enter username");

    entryPanel.add(usernameTextbox, c);

    // Create hub id textbox
    c.gridx = 0;
    c.gridy = 2;
    c.gridheight = 1;
    c.gridwidth = 2;

    hubID = new JTextField("Enter Hub ID");

    entryPanel.add(hubID, c);

    // Create and add join button

    c.gridx = 1;
    c.gridy = 3;
    c.gridheight = 1;
    c.gridwidth = 1;

    JButton joinButton = new JButton("Join Hub");
    joinButton.addActionListener(new JoinButtonListener());
    joinButton.setSize(100,20);

    entryPanel.add(joinButton, c);

    // Create and add create buttion

    c.gridx = 0;
    c.gridy = 3;
    c.gridheight = 1;
    c.gridwidth = 1;

    JButton createButton = new JButton("Create a Hub");
    createButton.addActionListener(new CreateButtonListener());
    createButton.setSize(100,20);

    entryPanel.add(createButton, c);

    // Add entry panel

    c.gridx = 2;
    c.gridy = 1;
    c.gridheight = 2;
    c.gridwidth = 3;
    c.fill = GridBagConstraints.NONE;

    gridbag.setConstraints(entryPanel, c);
    this.add(entryPanel);

	}

}
