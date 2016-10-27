/**
 * 
 */
package curves;

import java.math.BigInteger;

/**
 * @author root
 *
 * p=8884933102832021670310856601112383279507496491807071433260928721853918699951
 * n=8884933102832021670310856601112383279454437918059397120004264665392731659049
 * a4=2481513316835306518496091950488867366805208929993787063131352719741796616329
 * a6=4387305958586347890529260320831286139799795892409507048422786783411496715073
 * r4=5473953786136330929505372885864126123958065998198197694258492204115618878079
 * r6=5831273952509092555776116225688691072512584265972424782073602066621365105518
 * gx=7638166354848741333090176068286311479365713946232310129943505521094105356372
 * gy=762687367051975977761089912701686274060655281117983501949286086861823169994
 * r=8094458595770206542003150089514239385761983350496862878239630488323200271273
 *
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
	
	private String _name;
	
	public Curve(BigInteger p, BigInteger n, BigInteger a4, BigInteger a6,
			BigInteger r4, BigInteger r6, BigInteger gx, BigInteger gy,
			BigInteger r) {
		
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

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }
}
