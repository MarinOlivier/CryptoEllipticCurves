package crypto;

import curves.Curve;
import curves.Point;

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

    public DSA(Curve C, Point G, String name) {
        _G = G;
        _C = C;
        _s = randBigInt(C.getN());
        _Q = G.mult(_s);
    }

    public Point signDSA(String m) {
        BigInteger k = randBigInt(_C.getN());
        Point G = _G.mult(k);

        if(G.getX().equals(new BigInteger("0")))
            return signDSA(m);

        BigInteger x = G.getX();
        BigInteger y = ((SHA512(m).add(_s.multiply(x))).multiply(k.modInverse(_C.getN()))).mod(_C.getN());

        if(y.equals(new BigInteger("0")))
            return signDSA(m);

        return new Point(_C, x, y, false);
    }

    public boolean verifyDSA(Point P, String m) {
        BigInteger a = (SHA512(m).multiply(P.getY().modInverse(_C.getN()))).mod(_C.getN());
        Point T = _G.mult(a);
        BigInteger b = (P.getX().multiply(P.getY().modInverse(_C.getN()))).mod(_C.getN());
        Point Tp = _otherPub.mult(b);
        T = T.add(Tp);
        return P.getX().equals(T.getX());
    }

    public void setOtherPub(Point _otherPub) {
        this._otherPub = _otherPub;
    }

    public Point getQ() {
        return _Q;
    }
}
