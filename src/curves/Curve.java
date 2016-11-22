/**
 * 
 */
package curves;

import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.math.BigInteger;

/**
 * @author root
 *
 */
public class Curve {
	private BigInteger _p;
	private BigInteger _n;
	private BigInteger _a4;
	private BigInteger _a6;
	private BigInteger _r4;
	private BigInteger _r6;
	private BigInteger _gx;
	private BigInteger _gy;
	private BigInteger _r;

	public Curve(String dir, String filename) {
        BufferedReader br = null;

        try {

            String sCurrentLine;
            String sCurrentNumber;
//            br = new BufferedReader(new FileReader("/Users/oliviermarin/Documents/Polytech/Info5/CryptoAv/Curves/elliptic_curves/Weierstrass/"+dir+"/"+filename));
            br = new BufferedReader(new FileReader("D:\\Documents\\Polytech\\info5\\cryptoAv\\Curves\\elliptic_curves\\Weierstrass\\"+dir+"\\"+filename));
            int i = 1;

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                sCurrentNumber = sCurrentLine.substring(sCurrentLine.lastIndexOf("=")+1);
                switch (i){
                    case 1:
                        _p = new BigInteger(sCurrentNumber);
                        break;
                    case 2:
                        _n = new BigInteger(sCurrentNumber);
                        break;
                    case 3:
                        _a4 = new BigInteger(sCurrentNumber);
                        break;
                    case 4:
                        _a6 = new BigInteger(sCurrentNumber);
                        break;
                    case 5:
                        _r4 = new BigInteger(sCurrentNumber);
                        break;
                    case 6:
                        _r6 = new BigInteger(sCurrentNumber);
                        break;
                    case 7:
                        _gx = new BigInteger(sCurrentNumber);
                        break;
                    case 8:
                        _gy = new BigInteger(sCurrentNumber);
                        break;
                    case 9:
                        _r = new BigInteger(sCurrentNumber);
                        break;
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}

    public BigInteger getP() {
        return _p;
    }

    public void setP(BigInteger p) {
        _p = p;
    }

    public BigInteger getN() {
        return _n;
    }

    public void setN(BigInteger n) {
        _n = n;
    }

    public BigInteger getA4() {
        return _a4;
    }

    public void setA4(BigInteger a4) {
        _a4 = a4;
    }

    public BigInteger getA6() {
        return _a6;
    }

    public void setA6(BigInteger a6) {
        _a6 = a6;
    }

    public BigInteger getR4() {
        return _r4;
    }

    public void setR4(BigInteger r4) {
        _r4 = r4;
    }

    public BigInteger getR6() {
        return _r6;
    }

    public void setR6(BigInteger r6) {
        _r6 = r6;
    }

    public BigInteger getGx() {
        return _gx;
    }

    public void setGx(BigInteger gx) {
        _gx = gx;
    }

    public BigInteger getGy() {
        return _gy;
    }

    public void setGy(BigInteger gy) {
        _gy = gy;
    }

    public BigInteger getR() {
        return _r;
    }

    public void setR(BigInteger r) {
        _r = r;
    }
}
