package crypto;

import curves.Curve;
import curves.Point;
import main.ChatMessage;
import main.Client;
import main.ClientThread;

import java.math.BigInteger;

import static math.MathBigInt.*;

/**
 * Created by Olivier on 30/10/2016.
 */
public class ElGamal {
    private Point _P;
    private BigInteger _x;
    public Point _pubK;
    private Curve _c;
    public Point otherPubKey;

    public ElGamal(Point p, Curve c, String s) {
        _P = p;
        _c = c;
        _x = randBigInt(_c.getN());
        _pubK = _P.mult(_x);
        System.out.println(s + "\n Pub key -> " + _pubK.getX());
    }

    private Point F(String m) {
        System.out.println("Message = " + m);
        BigInteger x = toBigInteger(m);
        System.out.println("Message BigInt " + x);
        BigInteger y;
        y = (x.pow(3).add(_c.getA4().multiply(x)).add(_c.getA6())).mod(_c.getP());
        System.out.println("y2 = " + y);
        y = sqrtP(y, _c.getP());
        System.out.println("y = " + y);
        return new Point(_c, x, y, false);
    }

    private String invF(Point p) {
        System.out.println("Message BigInt received " + p.getX());
        return fromBigInteger(p.getX());
    }

    public boolean sendPubKToClient(ClientThread thread){
        String pubK = _pubK.getX().toString()+"|"+_pubK.getY().toString()+"|"+_pubK.isInf();
        thread.writeMsg(new ChatMessage(ChatMessage.EGPUBK, pubK));
        return true;
    }

    public void setReceivedPoint(Point receivedPoint, String s) {
        otherPubKey = receivedPoint;
        System.out.println(s + "\n Recei -> " + otherPubKey.getX());
    }

    public String cipher(String m) {
        BigInteger k = randBigInt(_c.getN());
        Point C1 = _P.mult(k);
        Point C2 = otherPubKey.mult(k);
        Point Pm = F(m);

        return C1.getX() + "|" + C1.getY() + "|" + C2.add(Pm).getX() + "|" + C2.add(Pm).getY();
    }

    public String uncipher(String cipher) {
        String[] tab;
        tab = cipher.split("\\|");
        BigInteger C1x = new BigInteger(tab[0]);
        BigInteger C1y = new BigInteger(tab[1]);
        BigInteger Dx = new BigInteger(tab[2]);
        BigInteger Dy = new BigInteger(tab[3]);

        Point C = new Point(_c, C1x, C1y, false);
        Point D = new Point(_c, Dx, Dy, false);
        Point Cp = C.mult(_x);

        Point Pm = D.add(Cp.opposite());
        return invF(Pm);
    }

}
