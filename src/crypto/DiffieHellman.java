package crypto;

import curves.Point;
import main.ChatMessage;
import main.*;

/**
 * Created by oliviermarin on 27/10/2016.
 */
public class DiffieHellman {
    private Point _P;
    private int _rand;
    private Point _calculatedPoint;
    private Point _receivedPoint;
    private Point _secKey;

    public DiffieHellman(Point P, int rand, String s) {
        _rand = rand;
        _P = new Point(P);
        _calculatedPoint = _P.mult(_rand);

        System.out.println(s + " calcP " + _calculatedPoint);


    }

    public boolean sendPointToClient(ClientThread thread){
        String point = _calculatedPoint.getX().toPlainString()+"|"+_calculatedPoint.getY().toPlainString()+"|"+_calculatedPoint.isInf();
        thread.writeMsg(new ChatMessage(ChatMessage.POINT, point));
        return true;
    }

    public boolean sendPointToServ(Client client) {
        String point = _calculatedPoint.getX().toPlainString()+"|"+_calculatedPoint.getY().toPlainString()+"|"+_calculatedPoint.isInf();
        client.sendMessage(new ChatMessage(ChatMessage.POINT, point));
        return true;
    }

    public void setReceivedPoint(Point receivedPoint) {
        _receivedPoint = receivedPoint;
    }

    public void setSecKey(String s) {
        System.out.println(s + " receivP " + _receivedPoint);
        _secKey = _receivedPoint.mult(_rand);
        System.out.println(s + " _secKey = " + _secKey);
    }
}
