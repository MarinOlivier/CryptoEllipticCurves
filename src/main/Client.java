package main;

import crypto.DSA;
import crypto.DiffieHellman;
import crypto.ElGamal;
import crypto.STS;
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
    private boolean isDSAinit;
    private boolean isSTSinit;

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
        isDSAinit = false;
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
            boolean DSAauthSent = false;
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
                            // msg = msg +","; <-- Test : sign wouldn't works : PASSED
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
                            cg.DH = new DiffieHellman(new Point(cg.getC(), cg.getC().getGx(), cg.getC().getGy(), false), username);
                            cg.DH.setReceivedPoint(new Point(cg.getC(), msg), username);
                            sendMessage(new ChatMessage(ChatMessage.STARTDH, cg.DH.getPubK(), cg.client));
                        } else {
                            cg.DH.setReceivedPoint(new Point(cg.getC(), msg), username);
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
                        cg.append("Starting ElGamal encryption.", "");
                        if(!isEGinit) {
                            Point P = new Point(cg.getC(), cg.getC().getGx(), cg.getC().getGy(), false);
                            cg.EG = new ElGamal(P, cg.getC(),username, cg.getPrivK(), cg.getPubK());
                            cg.EG.setReceivedPoint(new Point(cg.getC(), msg), username);
                            sendMessage(new ChatMessage(ChatMessage.STARTEG, cg.EG.getPubK(), cg.client));
                        } else {
                            cg.EG.setReceivedPoint(new Point(cg.getC(), msg), username);
                            isEGinit = false;
                        }
                        cg.getEGStartBut().setText("Stop EG");
                        cg.getDHStartBut().setEnabled(false);
                        cg.getDSABut().setEnabled(false);
                        cg.getStsBut().setEnabled(false);
                        cg.inEG = true;
                    }
                    if(type == ChatMessage.STOPEG) {
                        cg.inEG = false;
                        cg.getDHStartBut().setEnabled(true);
                        cg.getEGStartBut().setEnabled(true);
                        cg.getDSABut().setEnabled(true);
                        cg.getStsBut().setEnabled(true);
                        cg.getEGStartBut().setText("Start EG");
                        cg.append("Stopping ElGamel encryption.", "");
                    }
                    if(type == ChatMessage.STARTDSA) {
                        //cg.append("All messages will be signed.", "");
                        msg = msg.split("/")[1];
                        if(!isDSAinit) {
                            Point G = new Point(cg.getC(), cg.getC().getGx(), cg.getC().getGy(), false);
                            cg.Dsa = new DSA(cg.getC(), G, username, cg.getPrivK(), cg.getPubK());
                            cg.Dsa.setOtherPub(new Point(cg.getC(), msg));
                            sendMessage(new ChatMessage(ChatMessage.STARTDSA, cg.Dsa.getPubK(), cg.client));
                        } else {
                            cg.Dsa.setOtherPub(new Point(cg.getC(), msg));
                            isDSAinit = false;
                        }
                        cg.getDSABut().setText("Stop DSA");
                        cg.getDHStartBut().setEnabled(false);
                        cg.getEGStartBut().setEnabled(false);
                        cg.getStsBut().setEnabled(false);
                        cg.inDSA = true;
                    }
                    if(type == ChatMessage.DSAPUBK) {
                        sleep(500);
                        cg.Dsa.setOtherPub(new Point(cg.getC(), msg));
                    }
                    if(type == ChatMessage.DSASIGN){
                        cg.Dsa.setSign(msg);
                    }
                    if(type == ChatMessage.STSINIT) {
                        cg.append(username + " start STS", "");
                        msg = msg.split("/")[1];
                        if(!isSTSinit){
                            Point G = new Point(cg.getC(), cg.getC().getGx(), cg.getC().getGy(), false);
                            cg.Sts = new STS(cg.getC(), G, cg.client, false, username);
                            cg.Sts.receiveOtherG(new Point(cg.getC(), msg));
                            cg.Sts.calcK();
                            String sig = cg.Sts.sign();
                            String enc = cg.Sts.encrypt(cg.Sts.getK(), sig);

                            System.out.println(username + " : proceed to auth myself.");

                            sendMessage(new ChatMessage(ChatMessage.STSINIT, cg.Sts.getG(), cg.client));
                            sendMessage(new ChatMessage(ChatMessage.STSENC, enc, cg.client));

                        }
                        else {
                            cg.Sts.receiveOtherG(new Point(cg.getC(), msg));
                            cg.Sts.calcK();
                        }
                        cg.getDHStartBut().setEnabled(false);
                        cg.getEGStartBut().setEnabled(false);
                        cg.getDSABut().setEnabled(false);
                    }
                    if(type == ChatMessage.STSENC) {
                        if(isSTSinit) {
                            String sig = cg.Sts.decrypt(cg.Sts.getK(), msg);

                            if (cg.Sts.verify(sig)) {
                                System.out.println(username + " : other is auth. :)");
                                System.out.println(username + " : proceed to auth myself.");

                                sig = cg.Sts.sign();
                                String enc = cg.Sts.encrypt(cg.Sts.getK(), sig);
                                sendMessage(new ChatMessage(ChatMessage.STSENC, enc, cg.client));
                            }
                        } else {
                            String sig = cg.Sts.decrypt(cg.Sts.getK(), msg);
                            System.out.println(username + " : " + cg.Sts.verify(sig));
                        }
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
            String[] tab = msg.split("/");
            String msgName ="";
            if(tab.length>1) {
                String name = tab[0];
                msg = tab[1];
                msgName = name + " : " + msg;
            }
            if(verified)
                cg.append(msgName + "\nSignature OK.", "");
            else
                cg.append(msgName + "\nSignature ERROR.", "");
        }
    }

    public void setDHinit(boolean DHinit) {
        isDHinit = DHinit;
    }

    public void setEGinit(boolean EGinit) {
        isEGinit = EGinit;
    }

    public void setDSAinit(boolean DSAinit) {
        isDSAinit = DSAinit;
    }

    public void setSTSinit(boolean STSinit) {
        isSTSinit = STSinit;
    }

    public ClientGUI getCg() {
        return cg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return username.equals(client.username);

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public String getUsername() {
        return username;
    }
}
