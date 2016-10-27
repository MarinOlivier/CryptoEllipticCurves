/**
 * 
 */
package curves;
import java.math.BigDecimal;
import java.math.MathContext;


/**
 * @author root
 *
 */
public class Point {
    private Curve _curve;
	private BigDecimal _x;
	private BigDecimal _y;
	private boolean _inf; // true => is infinite point
	
    public Point(Curve curve, BigDecimal x, BigDecimal y, boolean inf){
		_curve = curve;
        _x = x;
		_y = y;
		_inf = inf;
	}

    public Point() {
        _curve = null;
		_x = new BigDecimal(0, MathContext.DECIMAL64);
		_y = new BigDecimal(0, MathContext.DECIMAL64);
		_inf = false;
	}

    public Point(Curve curve, boolean inf) {
        _curve = curve;
        _x = new BigDecimal(0, MathContext.DECIMAL64);
        if(inf)
            _y = new BigDecimal(1, MathContext.DECIMAL64);
        else
            _y = new BigDecimal(0, MathContext.DECIMAL64);
        this._inf = inf;
    }

    private boolean isOpposite(Point q) {
        return (q._x.equals(_x) && q._y.equals(_x.add(_y)));
    }

    @Override
    public boolean equals(Object obj) {
        Point q = (Point)obj;
        return (_x.equals(q._x) && _y.equals(_y));
    }

    public Point opposite() {
		Point q = new Point(_curve, false);
		q._x = _x;
		q._y = _x.add(_y);
		return q;
	}


	private Point add_distinct(Point q) {
        BigDecimal bot = _x.add(q._x);
        BigDecimal l = (_y.add(q._y)).divide(bot, MathContext.DECIMAL64);

        Point R = new Point();
        R._curve = _curve;
        R._x = (l.pow(2)).add(l).add(bot);
        R._y = (l.add(new BigDecimal(1, MathContext.DECIMAL64))).multiply(R._x).add(l.multiply(_x)).add(_y);

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

        BigDecimal l = _x.add(_y.divide(_x, MathContext.DECIMAL64));
        BigDecimal xr = l.pow(2).add(l);
        BigDecimal yr = _x.pow(2).add(l.multiply(xr)).add(xr);

        return new Point(_curve, xr, yr, _inf);
    }

    public Point mult(int k){
        String bin = Integer.toBinaryString(k);
        Point q = new Point(_curve, true);

        for(int i=bin.length()-1; i>=0; i--){
            q.add(q);
            if(bin.charAt(i) == '1'){
                q = q.add(this);
            }
        }
        return q;
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
