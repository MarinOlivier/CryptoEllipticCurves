package crypto;

import curves.Point;
import main.ChatMessage;
import main.*;

import static main.Main.C;
import static math.MathBigInt.*;

import java.math.BigInteger;

/**
 * Created by oliviermarin on 27/10/2016.
 */
public class DiffieHellman {
    private Point _P;
    private BigInteger _rand;
    private Point _calculatedPoint;
    private Point _receivedPoint;
    private Point _secKey;

    public DiffieHellman(Point P, String s) {
        _rand = randBigInt(C.getP());
        _P = new Point(P);

        if (s.equals("Alice"))
            System.out.println("P = " + P.getX());

        _calculatedPoint = _P.mult(_rand);

        System.out.println(s + " (" + _rand + " * " + _P.getX() + ")\n Calc -> " + _calculatedPoint.getX());
    }

    public boolean sendPointToClient(ClientThread thread){
        String point = _calculatedPoint.getX().toString()+"|"+_calculatedPoint.getY().toString()+"|"+_calculatedPoint.isInf();
        thread.writeMsg(new ChatMessage(ChatMessage.POINT, point));
        return true;
    }

    public boolean sendPointToServ(Client client) {
        String point = _calculatedPoint.getX().toString()+"|"+_calculatedPoint.getY().toString()+"|"+_calculatedPoint.isInf();
        client.sendMessage(new ChatMessage(ChatMessage.POINT, point));
        return true;
    }

    public void setReceivedPoint(Point receivedPoint, String s) {
        _receivedPoint = receivedPoint;
        System.out.println(s + "\n Recei -> " + _receivedPoint.getX());
    }

    public void setSecKey(String s) {
        _secKey = _receivedPoint.mult(_rand);
        System.out.println("\n" + s + " (" + _rand + " * " + _receivedPoint.getX() + ")\n Key -> " + _secKey.getX());
    }

    public Point getSecKey() {
        return _secKey;
    }
}
