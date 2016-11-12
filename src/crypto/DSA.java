package crypto;

import curves.Curve;
import curves.Point;
import main.ChatMessage;
import main.Client;
import main.ClientThread;
import main.Main;

import java.math.BigInteger;

import static math.MathBigInt.SHA512;
import static math.MathBigInt.randBigInt;

/**
 * Created by oliviermarin on 08/11/2016.
 */
public class DSA {
    private Curve _C;
    private BigInteger _s;
    private Point _Q;
    private Point _G;
    private Point _otherPub;
    public Point _sign;

    public DSA(Curve C, Point G, String name) {
        _G = G;
        _C = C;
        _s = randBigInt(C.getN());
        _Q = G.mult(_s);
    }

    public boolean sendPubKToClient(ClientThread thread){
        String pubK = _Q.getX().toString()+"|"+_Q.getY().toString()+"|"+_Q.isInf();
        thread.writeMsg(new ChatMessage(ChatMessage.DSAPUBK, pubK));
        return true;
    }

    public boolean sendPubKToServ(Client client) {
        String point = _Q.getX().toString()+"|"+_Q.getY().toString()+"|"+_Q.isInf();
        client.sendMessage(new ChatMessage(ChatMessage.DSAPUBK, point));
        return true;
    }

    public String signDSA(String m) {
        BigInteger k = randBigInt(_C.getN());
        Point G = _G.mult(k);

        if(G.getX().equals(new BigInteger("0")))
            return signDSA(m);

        BigInteger x = G.getX();
        BigInteger y = ((SHA512(m).add(_s.multiply(x))).multiply(k.modInverse(_C.getN()))).mod(_C.getN());

        if(y.equals(new BigInteger("0")))
            return signDSA(m);

        Point P = new Point(_C, x, y, false);
        return P.getX() + "|" + P.getY();
    }

    public boolean verifyDSA(String m) {
        BigInteger a = (SHA512(m).multiply(_sign.getY().modInverse(_C.getN()))).mod(_C.getN());
        Point T = _G.mult(a);
        BigInteger b = (_sign.getX().multiply(_sign.getY().modInverse(_C.getN()))).mod(_C.getN());
        Point Tp = _otherPub.mult(b);
        T = T.add(Tp);
        return _sign.getX().equals(T.getX());
    }

    public void setOtherPub(Point _otherPub) {
        this._otherPub = _otherPub;
    }

    public Point getQ() {
        return _Q;
    }

    public void setSign(String s) {
        String[] tab;
        tab = s.split("\\|");
        BigInteger x = new BigInteger(tab[0]);
        BigInteger y = new BigInteger(tab[1]);
        _sign = new Point(Main.C, x, y, false);
    }
}
