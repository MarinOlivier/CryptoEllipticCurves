package gui;

import crypto.DSA;
import crypto.DiffieHellman;
import crypto.ElGamal;
import curves.Point;
import main.ChatMessage;
import main.Client;
import main.Main;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Thread.sleep;


/**
 * Created by oliviermarin on 27/10/2016.
 */
public class ClientGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    // will first hold "Username:", later on "Enter message"
    private JLabel label;
    // to hold the Username and later on the messages
    private JTextArea input;

    // for the chat room
    //private JTextArea chatBox;
    private JPanel chatBox;

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
    public DSA Dsa;
    public boolean inEG;
    public boolean inDSA;
    private int widht = 400;

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
        //chatBox = new JTextArea("Welcome to the Chat room\n");
        chatBox = new JPanel(new GridLayout(100000, 1));


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

        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        centerPanel.add(new JScrollPane(chatBox));
        //chatBox.setEditable(false);
        int inputHeight = 75;
        int height = 450;
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
        //chatBox.append(name + str);
        //chatBox.setCaretPosition(chatBox.getText().length() - 1);


        AbstractBorder brdrLeft = new TextBubbleBorder(Color.WHITE,1,10,8);
        AbstractBorder brdrRight = new TextBubbleBorder(Color.LIGHT_GRAY,1,10,8,false);

        JTextPane txt = new JTextPane();
        txt.setEditable(false);
        JPanel msgPane = new JPanel(new BorderLayout());

        txt.setText(name + str);
        txt.setMaximumSize(new Dimension(widht / 2, 10000));
        txt.setMinimumSize(new Dimension(widht / 2, 20));

        //txt.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));


        msgPane.setBorder(BorderFactory.createEmptyBorder(3, 2, 1, 2));

        if (name.equals(username + " : ")) {
            txt.setBorder(brdrRight);
            txt.setBackground(Color.LIGHT_GRAY);
            msgPane.add(txt, BorderLayout.EAST);
        }
        else {
            txt.setBorder(brdrLeft);
            txt.setBackground(Color.WHITE);
            msgPane.add(txt, BorderLayout.WEST);
        }

        chatBox.add(msgPane);
        chatBox.revalidate();
        chatBox.repaint();
    }

    // called by the GUI is the connection failed
    // we reset our buttons, label, textfield
    public void connectionFailed() {
        // don't react to a <CR> after the username
        sendBut.removeActionListener(this);
        connected = false;
    }

    private void login() {
        // empty username ignore it
        if (username.length() == 0)
            return;

        // try creating a new Client with GUI
        client = new Client(defaultHost, defaultPort, username, this);
        // test if we can start the Client
        if (!client.start())
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
        if (o == DHStartBut) {
            startDH();
            return;
        }
        if (o == EGStartBut && !inEG) {
            initElGamal();
            return;
        }
        if (o == EGStartBut && inEG) {
            stopElGamal();
            return;
        }
        if (inEG) {
            System.out.println("username = " + username);
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, EG.cipher(username+"/"+ input.getText())));
            append(input.getText() + "\n", username + " : ");
            input.setText("");
            return;
        }
        if (o == DSABut && !inDSA) {
            initDSA();
            return;
        }
        if (o == DSABut && inDSA) {
            stopDSA();
            return;
        }
        if(inDSA){
            client.sendMessage(new ChatMessage(ChatMessage.DSASIGN, Dsa.signDSA(input.getText())));

            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, input.getText()));
            append(input.getText() + "\n", username + " : ");
            input.setText("");
            return;
        }
        if (connected) {
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, username + "/" + input.getText()));
            append(input.getText() + "\n", username + " : ");
            input.setText("");
        }
    }

    private void startDH() {
        DH = new DiffieHellman(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), username);
        //client.sendMessage(new ChatMessage(ChatMessage.STARTDH, "INIT"));
        //DH.sendPoint(client);
        client.setDHinit(true);
        client.sendMessage(new ChatMessage(ChatMessage.STARTDH, DH.getPubK()));
    }

    private void initElGamal() {
        append("Starting ElGamal encryption.", "");
        EG = new ElGamal(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), Main.C, username);

        client.sendMessage(new ChatMessage(ChatMessage.STARTEG, EG.getPubK()));
        client.setEGinit(true);
    }

    private void stopElGamal() {
        client.sendMessage(new ChatMessage(ChatMessage.STOPEG, ""));
    }

    private void initDSA() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, "INIT"));
        inDSA = true;

        append("All messages will be signed.", "");

        DSABut.setText("Stop DSA");
        DHStartBut.setEnabled(false);
        EGStartBut.setEnabled(false);

        Point G = new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false);
        Dsa = new DSA(Main.C, G, "Alice");

        Dsa.sendPubKToServ(client);
    }

    private void stopDSA() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, "STOP"));
        //chatBox.append("Stop DSA.\n");

        inDSA = false;
        DSABut.setText("Start DSA");
        DHStartBut.setEnabled(true);
        EGStartBut.setEnabled(true);
    }

    public JButton getDHStartBut() {
        return DHStartBut;
    }

    public JButton getEGStartBut() {
        return EGStartBut;
    }

    public JButton getDSABut() {
        return DSABut;
    }
}
