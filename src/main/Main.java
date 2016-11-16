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

        ServerGUI serv = new ServerGUI(1337);
        ClientGUI cli = new ClientGUI("Alice");
	}

}
