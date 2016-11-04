package math;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by Olivier on 30/10/2016.
 */
public class MathBigInt {

    public BigInteger p;

    public MathBigInt(BigInteger p) {
        this.p = p;
    }

    public static BigInteger toBigInteger(String foo) {
        return new BigInteger(foo.getBytes());
    }

    public static String fromBigInteger(BigInteger bar) {
        return new String(bar.toByteArray());
    }

    public static BigInteger randBigInt(BigInteger n) {
        Random rnd = new Random();
        int nlen = n.bitLength();
        BigInteger nm1 = n.subtract(BigInteger.ONE);
        BigInteger r, s;
        do {
            s = new BigInteger(nlen + 100, rnd);
            r = s.mod(n);
        } while (s.subtract(r).add(nm1).bitLength() >= nlen + 100);

        return r;
    }
}
