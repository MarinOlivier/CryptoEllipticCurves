package gui;

import main.Server;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;


/**
 * Created by oliviermarin on 27/10/2016.
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
    private static final long serialVersionUID = 1L;
    // my server
    private Server server;

    private JPanel mainPane;


    // server constructor that receive the port to listen to for connection as parameter
    public ServerGUI(int port) {
        super("Chat Server");
        server = null;

        mainPane = new JPanel();
        JLabel state = new JLabel("Server is stopped");

        state.setFont(new Font("Helvetica",1,20));

        mainPane.add(state);

        add(mainPane);

        setSize(300, 100);
        setVisible(true);

        // ceate a new Server
        server = new Server(port, this);
        // and start it as a thread
        new ServerRunning().start();
        state.setText("Server is running");
        mainPane.add(state);
        mainPane.revalidate();
        mainPane.repaint();
    }

    public void appendEvent(String str, String name) {

    }

    // start or stop where clicked
    public void actionPerformed(ActionEvent e) {

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
    private class ServerRunning extends Thread {
        public void run() {
            server.start();         // should execute until if fails
            // the server failed
            appendEvent("Server crashed\n", "Bob : ");
            server = null;
        }
    }
}
