/**
 * 
 */
package main;

import crypto.DSA;
import curves.Curve;
import curves.Point;
import gui.ClientGUI;
import gui.ServerGUI;
import math.MathBigInt;

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

        //ServerGUI serv = new ServerGUI(1337);
        //ClientGUI cli = new ClientGUI("Alice");

		Point G = new Point(C, C.getGx(), C.getGy(), false);
		DSA A = new DSA(C, G, "Alice");
		DSA B = new DSA(C, G, "Bob");

		String m = "Hello";
		Point P = A.signDSA(m);

		B.setOtherPub(A.getQ());
		A.setOtherPub(B.getQ());

		System.out.println(B.verifyDSA(P,m));

		//Point P = new Point(C, C.getGx(), C.getGy(), false);
		//System.out.println("1234 * " + C.getGx() );
		//System.out.println(P.mult(1234));

	}

}
