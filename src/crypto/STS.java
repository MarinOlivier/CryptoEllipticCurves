package crypto;

import curves.Curve;
import curves.Point;
import main.ChatMessage;
import main.Client;

import javax.crypto.Cipher;
import java.util.Base64;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;


import static java.lang.Thread.sleep;
import static math.MathBigInt.fromBigInteger;
import static math.MathBigInt.randBigInt;

/**
 * Created by oliviermarin on 23/11/2016.
 */
public class STS {
    private Curve _C;
    private String _username;
    private DSA _DSA;
    private boolean _isInit;

    private Client _cli;

    private BigInteger _x;
    private Point _G;
    private Point _xG;
    private Point _otherG;
    private BigInteger _iv;

    private Point _k;


    public STS(Curve C, Point G, Client cli, boolean isinit,String name) {
        _isInit = isinit;
        _cli = cli;
        _username = name;
        if(_isInit) {
            try {
                _DSA = DSAinit();
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                sleep(500);
                _DSA = _cli.getCg().Dsa;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _C = C;
        _G = G;
        _x = randBigInt(C.getN());
        _xG = _G.mult(_x);
        _iv = randBigInt(C.getN());
    }

    public String getG() {
        return _username + "/" + _xG.getX() + "|" + _xG.getY() + "|" + _G.isInf();
    }

    public void calcK() {
        _k = _otherG.mult(_x);
    }

    public String getK() {
        return _k.getX() + "|" + _k.getY() + "|" + _k.isInf();
    }

    public void receiveOtherG(Point P) {
        _otherG = P;
    }

    public String sign(){
        String msg = _xG.getX() + "|" + _xG.getY() + "|" + _xG.isInf();
        msg += "!";
        msg += _otherG.getX() + "|" + _otherG.getY() + "|" + _otherG.isInf();
        return _DSA.signDSA(msg);
    }

    public String encrypt(String key, String value) {
        String initVector = fromBigInteger(_iv);
        try {
            key = key.substring(0, 16);
            initVector.substring(0, 16);
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"), 0, 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), 0, 16, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: " + Base64.getEncoder().encodeToString(encrypted));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return null;
    }

    public String decrypt(String key, String encrypted) {
        String initVector = fromBigInteger(_iv);
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"), 0, 16);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), 0, 16, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private DSA DSAinit() {
        return  _cli.getCg().initDSA();
    }

}
