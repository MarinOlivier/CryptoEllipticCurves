/**
 * 
 */
package curves;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.math.BigInteger;
import java.math.MathContext;


/**
 * @author root
 *
 */
public class Point {
    private Curve _curve;
	private BigInteger _x;
	private BigInteger _y;
	private boolean _inf; // true => is infinite point
	
    public Point(Curve curve, BigInteger x, BigInteger y, boolean inf){
		_curve = curve;
        _x = x;
		_y = y;
		_inf = inf;
	}

	public Point(Point P) {
        _curve = P.getCurve();
        _x = P.getX();
        _y = P.getY();
        _inf = P.isInf();
    }

    public Point() {
        _curve = null;
		_x = new BigInteger("0");
		_y = new BigInteger("0");
		_inf = false;
	}

	public Point(Curve c, String s) {
        _curve = c;
        String[] tab;
        tab = s.split("\\|");
        _x = new BigInteger(tab[0]);
        _y = new BigInteger(tab[1]);
        _inf = Boolean.parseBoolean(tab[2]);
    }

    public Point(Curve curve, boolean inf) {
        _curve = curve;
        _x = new BigInteger("0");
        if(inf)
            _y = new BigInteger("1");
        else
            _y = new BigInteger("0");
        this._inf = inf;
    }

    private boolean isOpposite(Point q) {
        return (q._x.equals(_x) && q._y.equals(_x.add(_y)));
    }

    @Override
    public boolean equals(Object obj) {
        Point q = (Point)obj;
        return (_x.equals(q._x) && _y.equals(q._y));
    }

    public Point opposite() {
		Point q = new Point(_curve, false);
		q._x = _x;
		q._y = _x.add(_y);
		return q;
	}


	private Point add_distinct(Point q) {
        BigInteger bot = _x.subtract(q._x).mod(_curve.getP());
        BigInteger l = ((_y.subtract(q._y)).divide(bot)).mod(_curve.getP());

        Point R = new Point();
        R._curve = _curve;
        R._x = ((l.pow(2)).subtract(_x).subtract(q._x)).mod(_curve.getP());
        R._y = (l.multiply(_x.subtract(R._x)).subtract(_y)).mod(_curve.getP());

        return R;
    }

    public  Point add(Point q) {
        if(_inf)
            return q;
        if(q._inf)
            return this;
        if(this.isOpposite(q))
            return new Point(_curve, true);
        if(this.equals(q))
            return this.doubl();
        return this.add_distinct(q);
    }

    public Point doubl() {

        BigInteger l = ((new BigInteger("3").multiply(_x.pow(2))).add(_curve.getA4())).multiply((_y.multiply(new BigInteger("2"))).modInverse(_curve.getP()));
        l = l.mod(_curve.getP());
        BigInteger xr = l.pow(2).subtract(_x.multiply(new BigInteger("2")));
        BigInteger yr = l.multiply(_x.subtract(xr)).subtract(_y);

        return new Point(_curve, xr.mod(_curve.getP()), yr.mod(_curve.getP()), _inf);
    }

    public Point mult(int k){
        String bin = Integer.toBinaryString(k);
        Point q = new Point(_curve, true);

        for(int i=0; i<=bin.length()-1; i++){
            q = q.doubl();
            if(bin.charAt(i) == '1'){
                q = q.add(this);
            }
        }
        return q;
    }

    public Curve getCurve() {
        return _curve;
    }

    public BigInteger getX() {
        return _x;
    }

    public BigInteger getY() {
        return _y;
    }

    public boolean isInf() {
        return _inf;
    }

    @Override
    public String toString() {
        return "Point{" +
                "_curve=" + _curve +
                ", _x=" + _x +
                ", _y=" + _y +
                ", _inf=" + _inf +
                '}';
    }
}
