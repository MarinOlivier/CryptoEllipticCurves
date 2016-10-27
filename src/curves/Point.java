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

	private boolean isOpposite(Point q) {
        return (q._x.equals(_x) && q._y.equals(_x.add(_y)));
    }

	public Point opposite() {
		if (this._inf)
			return new Point(_curve, _x, _y, _inf);

		Point q = new Point();
        q._curve = _curve;
		q._x = _x;
		q._y = _x.add(_y); // a3 = a1 = 0
		q._inf = false;
		return q;
	}


	private Point add_distinct(Point q) {
        BigDecimal l;
        if(_x.equals(q._x))
            l = (_x.pow(2).add(q._y)).divide(_x, MathContext.DECIMAL64);
        else {
            BigDecimal top = _y.add(q._y);
            BigDecimal bot = _x.add(q._x);
            l = top.divide(bot, MathContext.DECIMAL64);
        }

        BigDecimal xr = l.pow(2).add(l).add(_x).add(q._x);
        BigDecimal yr = (l.add(new BigDecimal(1, MathContext.DECIMAL64))).multiply(xr).add(l.multiply(_x)).add(_y);

        return new Point(_curve, xr, yr, false);
    }

    public  Point add(Point q) {
        return null;
    }

    public Point doubl() {
        if(_inf)
            return new Point(_curve, _x, _y, _inf);

        BigDecimal l = _x.add(_y.divide(_x, MathContext.DECIMAL64));
        BigDecimal xr = l.pow(2).add(l);
        BigDecimal yr = _x.pow(2).add(l.multiply(_x)).add(_x);

        return new Point(_curve, xr, yr, false);
    }

}
