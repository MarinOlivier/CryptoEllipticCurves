package math;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static BigInteger SHA512(String m) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] output = digest.digest(m.getBytes());
            digest.update(output);

            return new BigInteger(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
