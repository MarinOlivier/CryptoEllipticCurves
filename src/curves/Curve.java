/**
 * 
 */
package curves;

import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author root
 *
 */
public class Curve {
	private BigDecimal _p;
	private BigDecimal _n;
	private BigDecimal _a4;
	private BigDecimal _a6;
	private BigDecimal _r4;
	private BigDecimal _r6;
	private BigDecimal _gx;
	private BigDecimal _gy;
	private BigDecimal _r;

	public Curve(String dir, String filename) {
        BufferedReader br = null;

        try {

            String sCurrentLine;
            String sCurrentNumber;
            //br = new BufferedReader(new FileReader("/Users/oliviermarin/Documents/Polytech/Info5/CryptoAv/Curves/elliptic_curves/Weierstrass/"+dir+"/"+filename));
            br = new BufferedReader(new FileReader("D:\\Documents\\Polytech\\info5\\cryptoAv\\Curves\\elliptic_curves\\Weierstrass\\"+dir+"\\"+filename));
            int i = 1;

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                sCurrentNumber = sCurrentLine.substring(sCurrentLine.lastIndexOf("=")+1);
                switch (i){
                    case 1:
                        _p = new BigDecimal(sCurrentNumber);
                        break;
                    case 2:
                        _n = new BigDecimal(sCurrentNumber);
                        break;
                    case 3:
                        _a4 = new BigDecimal(sCurrentNumber);
                        break;
                    case 4:
                        _a6 = new BigDecimal(sCurrentNumber);
                        break;
                    case 5:
                        _r4 = new BigDecimal(sCurrentNumber);
                        break;
                    case 6:
                        _r6 = new BigDecimal(sCurrentNumber);
                        break;
                    case 7:
                        _gx = new BigDecimal(sCurrentNumber);
                        break;
                    case 8:
                        _gy = new BigDecimal(sCurrentNumber);
                        break;
                    case 9:
                        _r = new BigDecimal(sCurrentNumber);
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

    public BigDecimal getP() {
        return _p;
    }

    public void setP(BigDecimal p) {
        _p = p;
    }

    public BigDecimal getN() {
        return _n;
    }

    public void setN(BigDecimal n) {
        _n = n;
    }

    public BigDecimal getA4() {
        return _a4;
    }

    public void setA4(BigDecimal a4) {
        _a4 = a4;
    }

    public BigDecimal getA6() {
        return _a6;
    }

    public void setA6(BigDecimal a6) {
        _a6 = a6;
    }

    public BigDecimal getR4() {
        return _r4;
    }

    public void setR4(BigDecimal r4) {
        _r4 = r4;
    }

    public BigDecimal getR6() {
        return _r6;
    }

    public void setR6(BigDecimal r6) {
        _r6 = r6;
    }

    public BigDecimal getGx() {
        return _gx;
    }

    public void setGx(BigDecimal gx) {
        _gx = gx;
    }

    public BigDecimal getGy() {
        return _gy;
    }

    public void setGy(BigDecimal gy) {
        _gy = gy;
    }

    public BigDecimal getR() {
        return _r;
    }

    public void setR(BigDecimal r) {
        _r = r;
    }
}
