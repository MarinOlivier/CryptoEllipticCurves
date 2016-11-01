package gui;

import main.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * Created by oliviermarin on 27/10/2016.
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
    private static final long serialVersionUID = 1L;
    // the stop and start buttons
    private JButton stopStart;
    // JTextArea for the chat room and the events
    private JTextArea event;
    // The port number
    private JTextField tPortNumber;
    // my server
    private Server server;


    // server constructor that receive the port to listen to for connection as parameter
    public ServerGUI(int port) {
        super("Chat Server Bob");
        server = null;

        event = new JTextArea();
        event.setEditable(false);
        appendEvent("Events log.\n");
        add(event);

        // need to be informed when the user click the close button on the frame
        addWindowListener(this);
        setSize(400, 600);
        setVisible(true);

        // ceate a new Server
        server = new Server(port, this);
        // and start it as a thread
        new ServerRunning().start();
    }

    public void appendEvent(String str) {
        event.append(str);
    }

    // start or stop where clicked
    public void actionPerformed(ActionEvent e) {
        // if running we have to stop
        if(server != null) {
            server.stop();
            server = null;
            tPortNumber.setEditable(true);
            stopStart.setText("Start");
            return;
        }
        // OK start the server
        int port;
        try {
            port = Integer.parseInt(tPortNumber.getText().trim());
        }
        catch(Exception er) {
            appendEvent("Invalid port number");
            return;
        }
    }

    /*
     * If the user click the X button to close the application
     * I need to close the connection with the server to free the port
     */
    public void windowClosing(WindowEvent e) {
        // if my Server exist
        if(server != null) {
            try {
                server.stop();			// ask the server to close the conection
            }
            catch(Exception eClose) {
            }
            server = null;
        }
        // dispose the frame
        dispose();
        System.exit(0);
    }
    // I can ignore the other WindowListener method
    public void windowClosed(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /*
     * A thread to run the Server
     */
    class ServerRunning extends Thread {
        public void run() {
            server.start();         // should execute until if fails
            // the server failed
            stopStart.setText("Start");
            tPortNumber.setEditable(true);
            appendEvent("Server crashed\n");
            server = null;
        }
    }
}
