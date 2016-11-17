package main;

import crypto.DiffieHellman;
import crypto.ElGamal;
import curves.Point;
import gui.ClientGUI;

import java.net.*;
import java.io.*;

/**
 * Created by oliviermarin on 27/10/2016.
 */
public class Client {
    // for I/O
    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;
    private boolean isDHinit;
    private boolean isEGinit;

    // if I use a GUI or not
    private ClientGUI cg;

    // the server, the port and the username
    private String server, username;
    private int port;

    /*
     *  Constructor called by console mode
     *  server: the server address
     *  port: the port number
     *  username: the username
     */
    public Client(String server, int port, String username) {
        // which calls the common constructor with the GUI set to null
        this(server, port, username, null);
    }

    /*
     * Constructor call when used from a GUI
     * in console mode the ClienGUI parameter is null
     */
    public Client(String server, int port, String username, ClientGUI cg) {
        this.server = server;
        this.port = port;
        this.username = username;
        isDHinit = false;
        isEGinit = false;
        // save if we are in GUI mode or not
        this.cg = cg;
    }

    /*
     * To start the dialog
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        }
        // if it failed not much I can so
        catch(Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

		/* Creating both Data Stream */
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console or the GUI
     */
    private void display(String msg) {
        if(cg == null)
            System.out.println(msg);      // println in console mode
        else
            cg.append(msg + "\n", "");		// append to the ClientGUI JTextArea (or whatever)
    }

    /*
     * To send a message to the server
     */
    public void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {} // not much else I can do
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {} // not much else I can do
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {} // not much else I can do

        // inform the GUI
        if(cg != null)
            cg.connectionFailed();

    }

    /**
     * a class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ListenFromServer extends Thread {

        public void run() {
            while(true) {
                try {
                    ChatMessage chtMsg = (ChatMessage) sInput.readObject();
                    int type = chtMsg.getType();
                    String msg = chtMsg.getMessage();
                    if(type == ChatMessage.MESSAGE) {
                        if(cg.inEG){
                            String plain = cg.EG.uncipher(msg);
                            String[] tab = plain.split("/");
                            if(tab.length>1) {
                                String name = tab[0];
                                msg = tab[1];
                                String msgName = name + " : " + msg;
                                cg.append(msgName, "");
                            }
                        } else if(cg.inDSA){
                            boolean verified = cg.Dsa.verifyDSA(msg);
                            appendSignedMsg(verified, msg);
                        } else {
                            String[] tab = msg.split("/");
                            String name = tab[0];
                            msg = tab[1];
                            String msgName = name + " : " + msg;
                            cg.append(msgName, "");
                        }
                    }
                    if(type == ChatMessage.STARTDH){
                        msg = msg.split("/")[1];
                        if(!isDHinit) {
                            cg.DH = new DiffieHellman(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), username);
                            cg.DH.setReceivedPoint(new Point(Main.C, msg), username);
                            sendMessage(new ChatMessage(ChatMessage.STARTDH, cg.DH.getPubK()));
                        } else {
                            cg.DH.setReceivedPoint(new Point(Main.C, msg), username);
                            isDHinit = false;
                        }
                        cg.DH.setSecKey();
                        msg = "Secret key :";
                        msg += "\n  x = " + cg.DH.getSecKey().getX();
                        msg += "\n  y = " + cg.DH.getSecKey().getY();
                        cg.append(msg, "");
                    }
                    if(type == ChatMessage.STARTEG) {
                        msg = msg.split("/")[1];
                        if(!isEGinit) {
                            cg.append("Starting ElGamal encryption.", "");
                            cg.EG = new ElGamal(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), Main.C,username);
                            cg.EG.setReceivedPoint(new Point(Main.C, msg), username);
                            sendMessage(new ChatMessage(ChatMessage.STARTEG, cg.EG.getPubK()));
                        } else {
                            cg.EG.setReceivedPoint(new Point(Main.C, msg), username);
                            isEGinit = false;
                        }
                        cg.getEGStartBut().setText("Stop EG");
                        cg.getDHStartBut().setEnabled(false);
                        cg.getDSABut().setEnabled(false);
                        cg.inEG = true;
                    }
                    if(type == ChatMessage.STOPEG) {
                        cg.inEG = false;
                        cg.getDHStartBut().setEnabled(true);
                        cg.getEGStartBut().setEnabled(true);
                        cg.getDSABut().setEnabled(true);
                        cg.getEGStartBut().setText("Start EG");
                        cg.append("Stopping ElGamel encryption.", "");
                    }
                    if(type == ChatMessage.STARTDSA) {

                    }
                    if(type == ChatMessage.DSAPUBK) {
                        sleep(500);
                        cg.Dsa.setOtherPub(new Point(Main.C, msg));
                    }
                    if(type == ChatMessage.DSASIGN){
                        cg.Dsa.setSign(msg);
                    }
                }
                catch(IOException e) {
                    display("Server has close the connection: " + e);
                    if(cg != null)
                        cg.connectionFailed();
                    break;
                }
                // can't happen with a String object but need the catch anyhow
                catch(ClassNotFoundException e2) {
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void appendSignedMsg(boolean verified, String msg) {
            if(verified)
                cg.append(msg + "\nSignature OK.", "");
            else
                cg.append(msg + "\nSignature ERROR.", "");
        }
    }

    public void setDHinit(boolean DHinit) {
        isDHinit = DHinit;
    }

    public void setEGinit(boolean EGinit) {
        isEGinit = EGinit;
    }
}
