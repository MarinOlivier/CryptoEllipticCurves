package crypto;

import curves.Curve;
import curves.Point;

import java.math.BigInteger;

import static main.Main.C;
import static math.MathBigInt.randBigInt;

/**
 * Created by oliviermarin on 08/11/2016.
 */
public class DSA {
    private Curve _C;
    private BigInteger _x;
    private Point _Q;

    public DSA(Curve C, Point G, String name) {
        _C = C;
        _x = randBigInt(C.getP());
        _Q = G.mult(_x);
    }
}
