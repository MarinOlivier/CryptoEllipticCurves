package main;

/**
 * Created by Olivier on 28/10/2016.
 */

import crypto.DiffieHellman;
import crypto.ElGamal;
import curves.Point;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

/** One instance of this thread will run for each client */
public class ClientThread extends Thread {
    // the socket where to listen/talk
    Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    // my unique id (easier for deconnection)
    int id;
    // the Username of the Client
    String username;
    // the only type of message a will receive
    ChatMessage cm;
    // the date I connect
    String date;
    Server _srv;
    private DiffieHellman DH;
    private ElGamal EG;

    public ClientThread(Server srv, Socket socket) {
        _srv = srv;
        // a unique id
        id = ++_srv.uniqueId;
        this.socket = socket;
			/* Creating both Data Stream */
        System.out.println("Thread trying to create Object Input/Output Streams");
        try
        {
            // create output first
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput  = new ObjectInputStream(socket.getInputStream());
            // read the username
            username = (String) sInput.readObject();
            _srv.display(username + " just connected.");
        }
        catch (IOException e) {
            _srv.display("Exception creating new Input/output Streams: " + e);
            return;
        }
        // have to catch ClassNotFoundException
        // but I read a String, I am sure it will work
        catch (ClassNotFoundException e) {
        }
        date = new Date().toString() + "\n";
    }

    // what will run forever
    public void run() {
        // to loop until LOGOUT
        boolean keepGoing = true;
        while(keepGoing) {
            // read a String (which is an object)
            try {
                cm = (ChatMessage) sInput.readObject();
            }
            catch (IOException e) {
                _srv.display(username + " Exception reading Streams: " + e);
                break;
            }
            catch(ClassNotFoundException e2) {
                break;
            }
            // the messaage part of the ChatMessage
            String message = cm.getMessage();

            // Switch on the type of message receive
            switch(cm.getType()) {

                case ChatMessage.MESSAGE:
                    _srv.display(username + ": here" + message);
                    break;
                case ChatMessage.STARTDH:
                    _srv.display("Start DH exchange keys with :");
                    _srv.display("gx = " + Main.C.getGx());
                    _srv.display("gy = " + Main.C.getGy());
                    DH = new DiffieHellman(new curves.Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), 1234, "Bob");
                    DH.sendPointToClient(this);
                    break;
                case ChatMessage.POINT:
                    _srv.display("Received point is :");
                    _srv.display(message);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DH.setReceivedPoint(new Point(Main.C, message), "Bob");
                    DH.setSecKey("Bob");
                    _srv.display("Secret key is : " + DH.getSecKey());
                    break;
                case ChatMessage.STARTEG:
                    _srv.display("Start EG :");
                    EG = new ElGamal(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), Main.C, "Bob");
                    EG.sendPubKToClient(this);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case ChatMessage.EGPUBK:
                    EG.setReceivedPoint(new Point(Main.C, message), "Bob");
                    break;
                case ChatMessage.MSG_EG:
                    _srv.display("receive MSG_EG");
                    _srv.display(EG.uncipher(message));
                    break;
            }
        }
        // remove myself from the arrayList containing the list of the
        // connected Clients
        _srv.remove(id);
        close();
    }

    // try to close everything
    private void close() {
        // try to close the connection
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {};
        try {
            if(socket != null) socket.close();
        }
        catch (Exception e) {}
    }

    /*
     * Write a String to the Client output stream
     */
    public boolean writeMsg(ChatMessage chtMsg) {
        // if Client is still connected send the message to it
        if(!socket.isConnected()) {
            close();
            return false;
        }
        // write the message to the stream
        try {

            sOutput.writeObject(chtMsg);
        }
        // if an error occurs, do not abort just inform the user
        catch(IOException e) {
            _srv.display("Error sending message to " + username);
            _srv.display(e.toString());
        }
        return true;
    }
}
