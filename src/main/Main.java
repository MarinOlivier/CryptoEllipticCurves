/**
 * 
 */
package main;

import curves.Curve;
import curves.Point;
import gui.ClientGUI;
import gui.ServerGUI;

import java.math.BigInteger;
import java.math.MathContext;

/**
 * @author root
 *
 */
public class Main {

	public static Curve C;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        C = new Curve("cw256", "w256-001.gp");
        ServerGUI serv = new ServerGUI(1337);
        ClientGUI cli = new ClientGUI("Alice");

		//Point P = new Point(C, C.getGx(), C.getGy(), false);
		//System.out.println(P.mult(2));
		//System.out.println(P.add(P));
	}

}
