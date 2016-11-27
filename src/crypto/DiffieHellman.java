package crypto;

import curves.Curve;
import curves.Point;
import main.ChatMessage;
import main.*;

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
    private String _username;
    private Curve _c;

    public DiffieHellman(Point P, String s) {
        _username = s;
        _P = new Point(P);
        _c = _P.getCurve();
        _rand = randBigInt(_c.getP());

        _calculatedPoint = _P.mult(_rand);
        System.out.println(s + "\n  Calc : " + _calculatedPoint.getX());
    }

    public String getPubK(){
        return _username+"/"+_calculatedPoint.getX().toString()+"|"+_calculatedPoint.getY().toString()+"|"+_calculatedPoint.isInf();
    }

    public void setReceivedPoint(Point receivedPoint, String s) {
        _receivedPoint = receivedPoint;
        System.out.println(s + "\n Recei -> " + _receivedPoint.getX());
    }

    public void setSecKey() {
        _secKey = _receivedPoint.mult(_rand);
        System.out.println("\n" + _username + " (" + _rand + " * " + _receivedPoint.getX() + ")\n Key -> " + _secKey.getX());
    }

    public Point getSecKey() {
        return _secKey;
    }
}
