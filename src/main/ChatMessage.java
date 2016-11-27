package main;


import java.io.Serializable;

/**
 * Created by oliviermarin on 27/10/2016.
 */
public class ChatMessage implements Serializable {
    protected static final long serialVersionUID = 1112122200L;

    public static final int MESSAGE = 1, STARTDH = 2, POINT = 3, STARTEG = 4, EGPUBK = 5, MSG_EG = 6, STOPEG = 7, STARTDSA = 8,
            DSAPUBK = 9, DSASIGN = 10, STSINIT = 11, STSENC = 12;
    private int type;
    private String src;
    private String message;

    // constructor
    public ChatMessage(int type, String message, Client s) {
        this.type = type;
        this.message = message;
        src = s.getUsername();
    }

    // getters
    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getSrc() {
        return src;
    }
}
