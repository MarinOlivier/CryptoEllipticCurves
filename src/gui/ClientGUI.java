package gui;

import crypto.DiffieHellman;
import curves.*;
import main.ChatMessage;
import main.Client;
import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Created by oliviermarin on 27/10/2016.
 */
public class ClientGUI extends JFrame implements ActionListener{
    private static final long serialVersionUID = 1L;
    // will first hold "Username:", later on "Enter message"
    private JLabel label;
    // to hold the Username and later on the messages
    private JTextArea input;

    // for the chat room
    private JTextArea ta;
    private JButton sendBut;
    // if it is for connection
    private boolean connected;
    // the Client object
    private Client client;
    // the default port number
    private int defaultPort;
    private String defaultHost;
    private String username;

    private int inputHeight = 75;
    private int height = 450;
    private int widht = 400;
    private JButton DHStartBut;
    public DiffieHellman DH;

    // Constructor connection receiving a socket number
    public ClientGUI(String username) {
        super("Chat Client " + username);

        this.username = username;
        defaultPort = 1337;
        defaultHost = "localhost";

        input = new JTextArea();

        sendBut = new JButton("Send");

        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room\n");

        JPanel northPane = new JPanel(new GridLayout(1, 3));
        DHStartBut = new JButton("Start DH");

        DHStartBut.addActionListener(this);

        northPane.add(DHStartBut);
        add(northPane, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1,1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        centerPanel.setSize(widht, height-inputHeight);
        add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();

        southPanel.setBackground(Color.WHITE);
        southPanel.setLayout(new BorderLayout());
        southPanel.add(input, BorderLayout.CENTER);
        southPanel.add(sendBut, BorderLayout.EAST);

        southPanel.setSize(widht, inputHeight);

        add(southPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(widht, height);
        setVisible(true);
        input.requestFocus();

        login();
    }

    // called by the Client to append text in the TextArea
    public void append(String str) {
        ta.append(str);
        ta.setCaretPosition(ta.getText().length() - 1);
    }
    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    public void connectionFailed() {
        // don't react to a <CR> after the username
        sendBut.removeActionListener(this);
        connected = false;
    }

    private void login(){
        // empty username ignore it
        if(username.length() == 0)
            return;

        // try creating a new Client with GUI
        client = new Client(defaultHost, defaultPort, username, this);
        // test if we can start the Client
        if(!client.start())
            return;
        input.setText("");

        sendBut.addActionListener(this);
        connected = true;
    }

    /*
    *
    */
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o == DHStartBut) {
            client.sendMessage(new ChatMessage(ChatMessage.STARTDH, ""));
            ta.append("Start DH exchange keys with :\n");
            ta.append("gx = " + Main.C.getGx() +"\n");
            ta.append("gy = " + Main.C.getGy() +"\n");

            DH = new DiffieHellman(new curves.Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), 4567, "Alice");
            DH.sendPointToServ(client);
            return;
        }
        if (connected) {
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, input.getText()));
            input.setText("");
        }
    }

}
