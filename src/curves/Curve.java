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
            br = new BufferedReader(new FileReader("/Users/oliviermarin/Documents/Polytech/Info5/CryptoAv/Curves/elliptic_curves/Weierstrass/"+dir+"/"+filename));
            int i = 1;

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
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

    public BigDecimal get_p() {
        return _p;
    }

    public void set_p(BigDecimal _p) {
        this._p = _p;
    }

    public BigDecimal get_n() {
        return _n;
    }

    public void set_n(BigDecimal _n) {
        this._n = _n;
    }

    public BigDecimal get_a4() {
        return _a4;
    }

    public void set_a4(BigDecimal _a4) {
        this._a4 = _a4;
    }

    public BigDecimal get_a6() {
        return _a6;
    }

    public void set_a6(BigDecimal _a6) {
        this._a6 = _a6;
    }

    public BigDecimal get_r4() {
        return _r4;
    }

    public void set_r4(BigDecimal _r4) {
        this._r4 = _r4;
    }

    public BigDecimal get_r6() {
        return _r6;
    }

    public void set_r6(BigDecimal _r6) {
        this._r6 = _r6;
    }

    public BigDecimal get_gx() {
        return _gx;
    }

    public void set_gx(BigDecimal _gx) {
        this._gx = _gx;
    }

    public BigDecimal get_gy() {
        return _gy;
    }

    public void set_gy(BigDecimal _gy) {
        this._gy = _gy;
    }

    public BigDecimal get_r() {
        return _r;
    }

    public void set_r(BigDecimal _r) {
        this._r = _r;
    }
}
