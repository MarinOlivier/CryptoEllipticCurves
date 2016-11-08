package main;

import crypto.DiffieHellman;
import gui.ServerGUI;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by oliviermarin on 27/10/2016.
 */
public class Server {
    // a unique ID for each connection
    public int uniqueId;
    // an ArrayList to keep the list of the Client
    private ClientThread ct;
    // if I am in a GUI
    private ServerGUI sg;
    // to display time
    private SimpleDateFormat sdf;
    // the port number to listen for connection
    private int port;
    // the boolean that will be turned of to stop the server
    private boolean keepGoing;

    private DiffieHellman DH;


    /*
     *  server constructor that receive the port to listen to for connection as parameter
     *  in console
     */
    public Server(int port) {
        this(port, null);
    }

    public Server(int port, ServerGUI sg) {
        // GUI or not
        this.sg = sg;
        // the port
        this.port = port;
        // to display hh:mm:ss
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        //ct = new ClientThread();
    }

    public void start() {
        keepGoing = true;
		/* create socket server and wait for connection requests */
        try
        {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);

            // infinite loop to wait for connections
            while(keepGoing)
            {
                // format message saying we are waiting
                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if(!keepGoing)
                    break;
                ct = new ClientThread(this, socket);  // make a thread of it
                ct.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                ct.sInput.close();
                ct.sOutput.close();
                ct.socket.close();
            }
            catch(Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }
    /*
     * For the GUI to stop the server
     */
    public void stop() {
        keepGoing = false;
        // connect to myself as Client to exit statement
        // Socket socket = serverSocket.accept();
        try {
            new Socket("localhost", port);
        }
        catch(Exception e) {
            // nothing I can really do
        }
    }
    /*
     * Display an event (not a message) to the console or the GUI
     */
    public void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if(sg == null)
            System.out.println(time);
        else
            sg.appendEvent(time + "\n");
    }
    /*
     *  to send a message to all Clients
     */
    public synchronized void send(String message) {
        // add HH:mm:ss and \n to the message
        String time = sdf.format(new Date());
        String messageLf = time + " " + message + "\n";

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        if(ct.isInEG()){
            ct.writeMsg(new ChatMessage(ChatMessage.MESSAGE, ct.getEG().cipher(messageLf)));
        }
        else {
            ct.writeMsg(new ChatMessage(ChatMessage.MESSAGE, messageLf));
        }
    }

    public void setEnableSendBut(boolean b){
        sg.sendBut.setEnabled(b);
    }

}
