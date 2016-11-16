package main;

/**
 * Created by Olivier on 28/10/2016.
 */

import crypto.DSA;
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
    private DSA Dsa;
    private boolean inEG;
    private boolean inDSA;

    public ClientThread(Server srv, Socket socket) {
        _srv = srv;
        inEG = false;
        inDSA = false;
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
            _srv.display(username + " just connected.", "");
        }
        catch (IOException e) {
            _srv.display("Exception creating new Input/output Streams: " + e, "Bob");
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
                _srv.display(username + " Exception reading Streams: " + e, "Bob");
                break;
            }
            catch(ClassNotFoundException e2) {
                break;
            }
            // the messaage part of the ChatMessage
            String message = cm.getMessage();
            String[] tab;
            tab = message.split("\\|");
            String name = tab[0];
            message = tab[1];
            _srv.broadcast(name, message);
        }
        // remove myself from the arrayList containing the list of the
        // connected Clients
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
            _srv.display("Error sending message to " + username, "Bob");
            _srv.display(e.toString(), "Bob");
        }
        return true;
    }

    private void initDH(){
        DH = new DiffieHellman(new curves.Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), "Bob");
        DH.sendPointToClient(this);
    }

    private void calcDHkey(String message){
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DH.setReceivedPoint(new Point(Main.C, message), "Bob");
        DH.setSecKey("Bob");
        _srv.display("Secret key is :\nx = " + DH.getSecKey().getX() + "\ny = " + DH.getSecKey().getY() + "\n", "");
    }

    private void initElGamal(){
        _srv.display("Starting ElGamal encryption.", "");
        EG = new ElGamal(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), Main.C, "Bob");
        EG.sendPubKToClient(this);
    }

    private void stopElGamal(){
        inEG = false;
        _srv.display("Stopping ElGamal encryption.", "");
    }

    private void initDSA(){
        _srv.display("All messages will be signed.", "");
        inDSA = true;
        Point G = new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false);
        Dsa = new DSA(Main.C, G, "Bob");
        Dsa.sendPubKToClient(this);
    }

    private void stopDSA(){
        _srv.display("Stopping DSA check.", "");
        inDSA = false;
    }

    private void appendSignedMsg(boolean verified, String message) {
        if(verified)
            _srv.display(username + " : " + message + "\nSignature OK.", "");
        else
            _srv.display(username + " : " + message + "\nSignature ERROR.", "");
    }

    public boolean isInEG() {
        return inEG;
    }

    public boolean isInDSA() {
        return inDSA;
    }

    public ElGamal getEG() {
        return EG;
    }

    public DSA getDsa() {
        return Dsa;
    }
}
