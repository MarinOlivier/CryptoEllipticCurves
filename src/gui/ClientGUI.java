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
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

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

        if (name.equals("Alice : ")) {
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
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, EG.cipher(input.getText())));
            append(input.getText() + "\n", "Alice : ");
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
            append(input.getText() + "\n", "Alice : ");
            input.setText("");
            return;
        }
        if (connected) {
            System.out.println("inDSA = " + inDSA);
            client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, input.getText()));
            append(input.getText() + "\n", "Alice : ");
            input.setText("");
        }
    }

    private void startDH() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTDH, "INIT"));
        //chatBox.append("Start DH exchange keys with :\n");
        //chatBox.append("gx = " + Main.C.getGx() +"\n");
        //chatBox.append("gy = " + Main.C.getGy() +"\n");

        DH = new DiffieHellman(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), "Alice");
        DH.sendPointToServ(client);
    }

    private void initElGamal() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTEG, "INIT"));
        //chatBox.append("Start EG.\n");

        EG = new ElGamal(new Point(Main.C, Main.C.getGx(), Main.C.getGy(), false), Main.C, "Alice");
        EG.sendPubKToServ(client);

        EGStartBut.setText("Stop EG");
        DHStartBut.setEnabled(false);
        DSABut.setEnabled(false);
        sendBut.setEnabled(false);
    }

    private void stopElGamal() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTEG, "STOP"));
        //chatBox.append("Stop EG.\n");

        inEG = false;
        EGStartBut.setText("Start EG");
        DHStartBut.setEnabled(true);
        DSABut.setEnabled(true);
    }

    private void initDSA() {
        client.sendMessage(new ChatMessage(ChatMessage.STARTDSA, "INIT"));
        //chatBox.append("Sign all message with DSA.\n");
        inDSA = true;
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

    class TextBubbleBorder extends AbstractBorder {

        private Color color;
        private int thickness = 4;
        private int radii = 8;
        private int pointerSize = 7;
        private Insets insets = null;
        private BasicStroke stroke = null;
        private int strokePad;
        private int pointerPad = 4;
        private boolean left = true;
        RenderingHints hints;

        TextBubbleBorder(
                Color color) {
            new TextBubbleBorder(color, 4, 8, 7);
        }

        TextBubbleBorder(
                Color color, int thickness, int radii, int pointerSize) {
            this.thickness = thickness;
            this.radii = radii;
            this.pointerSize = pointerSize;
            this.color = color;

            stroke = new BasicStroke(thickness);
            strokePad = thickness / 2;

            hints = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = radii + strokePad;
            int bottomPad = pad + pointerSize + strokePad;
            insets = new Insets(pad, pad, bottomPad, pad);
        }

        TextBubbleBorder(
                Color color, int thickness, int radii, int pointerSize, boolean left) {
            this(color, thickness, radii, pointerSize);
            this.left = left;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            return getBorderInsets(c);
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x, int y,
                int width, int height) {

            Graphics2D g2 = (Graphics2D) g;

            int bottomLineY = height - thickness - pointerSize;

            RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
                    0 + strokePad,
                    0 + strokePad,
                    width - thickness,
                    bottomLineY,
                    radii,
                    radii);

            Polygon pointer = new Polygon();

            if (left) {
                // left point
                pointer.addPoint(
                        strokePad + radii + pointerPad,
                        bottomLineY);
                // right point
                pointer.addPoint(
                        strokePad + radii + pointerPad + pointerSize,
                        bottomLineY);
                // bottom point
                pointer.addPoint(
                        strokePad + radii + pointerPad + (pointerSize / 2),
                        height - strokePad);
            } else {
                // left point
                pointer.addPoint(
                        width - (strokePad + radii + pointerPad),
                        bottomLineY);
                // right point
                pointer.addPoint(
                        width - (strokePad + radii + pointerPad + pointerSize),
                        bottomLineY);
                // bottom point
                pointer.addPoint(
                        width - (strokePad + radii + pointerPad + (pointerSize / 2)),
                        height - strokePad);
            }

            Area area = new Area(bubble);
            area.add(new Area(pointer));

            g2.setRenderingHints(hints);

            // Paint the BG color of the parent, everywhere outside the clip
            // of the text bubble.
            Component parent = c.getParent();
            if (parent != null) {
                Color bg = parent.getBackground();
                Rectangle rect = new Rectangle(0, 0, width, height);
                Area borderRegion = new Area(rect);
                borderRegion.subtract(area);
                g2.setClip(borderRegion);
                g2.setColor(bg);
                g2.fillRect(0, 0, width, height);
                g2.setClip(null);
            }

            g2.setColor(color);
            g2.setStroke(stroke);
            g2.draw(area);
        }

    }
}
