package gui;

import crypto.DiffieHellman;
import crypto.ElGamal;
import curves.Point;
import main.ChatMessage;
import main.Client;
import main.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Thread.sleep;


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
    public JButton sendBut;
    // if it is for connection
    private boolean connected;
    // the Client object
    private Client client;
    // the default port number
    private int defaultPort;
    private String defaultHost;
    private String username;

    private JButton DHStartBut;
    private JButton EGStartBut;
    private JButton DSABut;

    public DiffieHellman DH;
    public ElGamal EG;
    public boolean inEG;
    public boolean inDSA;

    // Constructor connection receiving a socket number
    public ClientGUI(String username) {
        super("Chat Client " + username);

        this.username = username;
        defaultPort = 1337;
        defaultHost = "localhost";
        inEG = false;
        inDSA = false;

        input = new JTextArea();

        sendBut = new JButton("Send");

        // The CenterPanel which is the chat room
        ta = new JTextArea("Welcome to the Chat room\n");


        JPanel northPane = new JPanel(new GridLayout(1, 3));
        DHStartBut = new JButton("Start DH");
        EGStartBut = new JButton("Start EG");
        DSABut = new JButton("Start DSA");

        DHStartBut.addActionListener(this);
        EGStartBut.addActionListener(this);
        DSABut.addActionListener(this);

        northPane.add(DHStartBut);
        northPane.add(EGStartBut);
        northPane.add(DSABut);
        add(northPane, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1,1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        int inputHeight = 75;
        int height = 450;
        int widht = 400;
        centerPanel.setSize(widht, height - inputHeight);
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
    public void append(String str, String name) {
        ta.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ta.append(name + str);
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
            startDH();
            return;
        }
        if(o == EGStartBut && !inEG) {
            initElGamal();
            return;
        }
        if(o == EGStartBut && inEG) {
            stopElGamal();
            return;
        }
        if(inEG){
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, EG.cipher(input.getText())));
            append(input.getText() + "\n", "Alice : ");
            input.setText("");
            return;
        }
        if(o == DSABut && !inDSA){
            initDSA();
            return;
        }
        if(o == DSABut && inDSA){
            stopDSA();
            return;
        }
        if (connected) {
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, input.getText()));
            append(input.getText() + "\n", "Alice : ");
            input.setText("");
        }
    }

    private void startDH(){
        client.sendMessage(new ChatMessage(ChatMessage.STARTDH, "INIT"));
        ta.append("Start DH exchange keys with :\n");
        ta.append("gx = " + Main.C.getGx() +"\n");
        ta.append("gy = " + Main.C.getGy() +"\n");

        DH = new DiffieHellman(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), "Alice");
        DH.sendPointToServ(client);
    }

    private void initElGamal(){
        client.sendMessage(new ChatMessage(ChatMessage.STARTEG, "INIT"));
        ta.append("Start EG.\n");

        EG = new ElGamal(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), Main.C, "Alice");
        EG.sendPubKToServ(client);

        EGStartBut.setText("Stop EG");
        DHStartBut.setEnabled(false);
        DSABut.setEnabled(false);
        sendBut.setEnabled(false);
    }

    private void stopElGamal(){
        client.sendMessage(new ChatMessage(ChatMessage.STARTEG, "STOP"));
        ta.append("Stop EG.\n");

        inEG = false;
        EGStartBut.setText("Start EG");
        DHStartBut.setEnabled(true);
        DSABut.setEnabled(true);
    }

    private void initDSA(){
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, "INIT"));
        ta.append("Sign all message with DSA.\n");
        inDSA = true;
        DSABut.setText("Stop DSA");
        DHStartBut.setEnabled(false);
        EGStartBut.setEnabled(false);
    }

    private void stopDSA(){
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, "STOP"));
        ta.append("Stop DSA.\n");

        inDSA = false;
        DSABut.setText("Start DSA");
        DHStartBut.setEnabled(true);
        EGStartBut.setEnabled(true);
    }

}
