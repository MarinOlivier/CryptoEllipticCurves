package crypto;

import curves.Curve;
import curves.Point;
import main.ChatMessage;
import main.Client;
import main.ClientThread;

import java.math.BigInteger;

import static main.Main.C;
import static math.MathBigInt.*;

/**
 * Created by Olivier on 30/10/2016.
 */
public class ElGamal {
    private Point _G;
    private BigInteger _x;
    public Point _pubK;
    private Curve _c;
    public Point otherPubKey;
    private String _username;

    public ElGamal(Point p, Curve c, String s) {
        _G = p;
        _c = c;
        _x = randBigInt(_c.getN());
        _pubK = _G.mult(_x);
        _username = s;
    }

    public String getPubK() {
        return _username + "/" + _pubK.getX() + "|" + _pubK.getY() + "|" + _pubK.isInf();
    }

    public void setReceivedPoint(Point receivedPoint, String s) {
        otherPubKey = receivedPoint;
    }

    public String cipher(String m) {
        BigInteger k = randBigInt(_c.getN());
        Point C1 = otherPubKey.mult(k);

        BigInteger C1x = (toBigInteger(m).add(C1.getX())).mod(C.getP());
        Point C2 = _G.mult(k);

        return C1x + "|" + C2.getX() + "|" + C2.getY();
    }

    public String uncipher(String cipher) {
        String[] tab;
        tab = cipher.split("\\|");
        BigInteger C1 = new BigInteger(tab[0]);
        BigInteger C2x = new BigInteger(tab[1]);
        BigInteger C2y = new BigInteger(tab[2]);

        Point C2 = new Point(C, C2x, C2y, false);

        return fromBigInteger((C1.subtract(C2.mult(_x).getX())).mod(C.getP()));
    }

}
