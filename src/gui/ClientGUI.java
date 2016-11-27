package gui;

import crypto.DSA;
import crypto.DiffieHellman;
import crypto.ElGamal;
import crypto.STS;
import curves.Curve;
import curves.Point;
import main.ChatMessage;
import main.Client;
import main.Main;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;

import static java.lang.Thread.sleep;
import static math.MathBigInt.randBigInt;


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
    public Client client;
    // the default port number
    private int defaultPort;

    private Curve C;

    private String defaultHost;
    private String username;

    private JButton DHStartBut;
    private JButton EGStartBut;
    private JButton DSABut;
    private JButton StsBut;

    public DiffieHellman DH;
    public ElGamal EG;
    public DSA Dsa;
    public STS Sts;
    public boolean inEG;
    public boolean inDSA;
    private int widht = 400;

    private BigInteger privK;
    public Point pubK;

    // Constructor connection receiving a socket number
    public ClientGUI(String username, int port) {
        super("Chat Client " + username);

        this.username = username;
        defaultPort = port;
        defaultHost = "localhost";
        inEG = false;
        inDSA = false;

        input = new JTextArea();

        sendBut = new JButton("Send");

        // The CenterPanel which is the chat room
        //chatBox = new JTextArea("Welcome to the Chat room\n");
        chatBox = new JPanel(new GridLayout(100000, 1));


        JPanel northPane = new JPanel(new GridLayout(1, 4));
        DHStartBut = new JButton("Start DH");
        EGStartBut = new JButton("Start EG");
        DSABut = new JButton("Start DSA");
        StsBut = new JButton("Start STS");

        DHStartBut.addActionListener(this);
        EGStartBut.addActionListener(this);
        DSABut.addActionListener(this);
        StsBut.addActionListener(this);

        northPane.add(DHStartBut);
        northPane.add(EGStartBut);
        northPane.add(DSABut);
        northPane.add(StsBut);

        add(northPane, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        centerPanel.add(new JScrollPane(chatBox));

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

        System.out.println(name + " " + str);

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

        File f = new File("/Users/oliviermarin/Documents/Polytech/Info5/CryptoAv/.curve/"+username+".k");
        privK = BigInteger.ZERO;

        if(f.exists()) {
            // read param in file
            BufferedReader br = null;
            C = new Curve();
            try {

                String sCurrentLine;
                String sCurrentNumber;
                br = new BufferedReader(new FileReader(f));
                int i = 1;

                while ((sCurrentLine = br.readLine()) != null) {
                    sCurrentNumber = sCurrentLine.substring(sCurrentLine.lastIndexOf("=")+1);
                    switch (i){
                        case 1:
                            C.setP(new BigInteger(sCurrentNumber));
                            break;
                        case 2:
                            C.setN(new BigInteger(sCurrentNumber));
                            break;
                        case 3:
                            C.setA4(new BigInteger(sCurrentNumber));
                            break;
                        case 4:
                            C.setA6(new BigInteger(sCurrentNumber));
                            break;
                        case 5:
                            C.setR4(new BigInteger(sCurrentNumber));
                            break;
                        case 6:
                            C.setR6(new BigInteger(sCurrentNumber));
                            break;
                        case 7:
                            C.setGx(new BigInteger(sCurrentNumber));
                            break;
                        case 8:
                            C.setGy(new BigInteger(sCurrentNumber));
                            break;
                        case 9:
                            C.setR(new BigInteger(sCurrentNumber));
                            break;
                        case 10:
                            privK = new BigInteger((sCurrentNumber));
                    }
                    i++;
                }
                Point G = new Point(C, C.getGx(), C.getGy(), false);
                pubK = G.mult(privK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try{

                C = new Curve("cw256", "w256-001.gp");

                PrintWriter writer = new PrintWriter(f, "UTF-8");
                writer.println("p="+C.getP());
                writer.println("n="+C.getN());
                writer.println("a4="+C.getA4());
                writer.println("a6="+C.getA6());
                writer.println("r4="+C.getR4());
                writer.println("r6="+C.getR6());
                writer.println("gx="+C.getGx());
                writer.println("gy="+C.getGy());
                writer.println("r="+C.getR());

                privK = randBigInt(C.getN());
                writer.println("priv="+privK);

                Point G = new Point(C, C.getGx(), C.getGy(), false);
                pubK = G.mult(privK);

                writer.close();
            } catch (Exception e) {
                // do something
            }
        }
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
        if (o == StsBut) {
            startSTS();
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
            System.out.println(username + " : " + EG.cipher(input.getText()));
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, EG.cipher(username+"/"+ input.getText()), client));
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
            client.sendMessage(new ChatMessage(ChatMessage.DSASIGN, Dsa.signDSA(username + "/" + input.getText()), client));

            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, username + "/" + input.getText(), client));
            append(input.getText() + "\n", username + " : ");
            input.setText("");
            return;
        }
        if (connected) {
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, username + "/" + input.getText(), client));
            append(input.getText() + "\n", username + " : ");
            input.setText("");
        }
    }

    private void startDH() {
        DH = new DiffieHellman(new Point(C, C.getGx(), C.getGy(), false), username);
        //client.sendMessage(new ChatMessage(ChatMessage.STARTDH, "INIT"));
        //DH.sendPoint(client);
        client.setDHinit(true);
        client.sendMessage(new ChatMessage(ChatMessage.STARTDH, DH.getPubK(), client));
    }

    private void initElGamal() {
        //append("Starting ElGamal encryption.", "");
        Point P = new Point(C, C.getGx(), C.getGy(), false);
        EG = new ElGamal(P, C, username, privK, pubK);

        client.setEGinit(true);
        client.sendMessage(new ChatMessage(ChatMessage.STARTEG, EG.getPubK(), client));
    }

    private void stopElGamal() {
        client.sendMessage(new ChatMessage(ChatMessage.STOPEG, "", client));
    }

    public DSA initDSA() {
        Point G = new Point(C, C.getGx(), C.getGy(), false);
        Dsa = new DSA(C, G, username, privK, pubK);

        client.setDSAinit(true);
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, Dsa.getPubK(), client));

        return Dsa;
    }

    private void stopDSA() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, "STOP", client));
        //chatBox.append("Stop DSA.\n");

        inDSA = false;
        DSABut.setText("Start DSA");
        DHStartBut.setEnabled(true);
        EGStartBut.setEnabled(true);
        StsBut.setEnabled(true);
    }

    private void startSTS() {
        Point G = new Point(C, C.getGx(), C.getGy(), false);
        Sts = new STS(C, G, client, true, username);

        client.setSTSinit(true);
        client.sendMessage(new ChatMessage(ChatMessage.STSINIT, Sts.getG(), client));
    }

    public Curve getC() {
        return C;
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

    public JButton getStsBut() {
        return StsBut;
    }

    public BigInteger getPrivK() {
        return privK;
    }

    public Point getPubK() {
        return pubK;
    }
}
