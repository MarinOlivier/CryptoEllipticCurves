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
    // the stop and start buttons
    private JButton stopStart;
    // JTextArea for the chat room and the events
    private JTextArea event;
    // The port number
    private JTextField tPortNumber;
    // my server
    private Server server;
    private JTextArea input;
    public JButton sendBut;
    private int widht = 400;
    private JPanel chatBox;

    // server constructor that receive the port to listen to for connection as parameter
    public ServerGUI(int port) {
        super("Chat Server Bob");
        server = null;

        setLayout(new BorderLayout());
        event = new JTextArea();
        event.setEditable(false);
        chatBox = new JPanel(new GridLayout(100000, 1));

        JPanel centerPanel = new JPanel(new GridLayout(1,1));
        //centerPanel.add(new JScrollPane(event));
        centerPanel.add(new JScrollPane(chatBox));
        add(centerPanel, BorderLayout.CENTER);

        input = new JTextArea();

        sendBut = new JButton("Send");

        JPanel southPanel = new JPanel();

        sendBut.addActionListener(this);

        southPanel.setBackground(Color.WHITE);
        southPanel.setLayout(new BorderLayout());
        southPanel.add(input, BorderLayout.CENTER);
        southPanel.add(sendBut, BorderLayout.EAST);

        int inputHeight = 75;
        int widht = 400;

        southPanel.setSize(widht, inputHeight);

        add(southPanel, BorderLayout.SOUTH);

        // need to be informed when the user click the close button on the frame
        addWindowListener(this);
        setSize(widht, 600);
        setVisible(true);

        // ceate a new Server
        server = new Server(port, this);
        // and start it as a thread
        new ServerRunning().start();
    }

    public void appendEvent(String str, String name) {
        //event.append(str);
        AbstractBorder brdrLeft = new TextBubbleBorder(Color.WHITE,1,10,8);
        AbstractBorder brdrRight = new TextBubbleBorder(Color.LIGHT_GRAY,1,10,8,false);

        JTextPane txt = new JTextPane();
        txt.setEditable(false);
        JPanel msgPane = new JPanel(new BorderLayout());

        txt.setText(name + str);
        txt.setMaximumSize(new Dimension(widht / 2, 10000));
        txt.setMinimumSize(new Dimension(widht / 2, 20));

        if (name.equals("Bob : ")) {
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

    // start or stop where clicked
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if(o == sendBut){
            server.send("Bob : " + input.getText());
            server.display(input.getText(), "Bob : ");
            input.setText("");
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
            appendEvent("Server crashed\n", "Bob : ");
            server = null;
        }
    }
}
