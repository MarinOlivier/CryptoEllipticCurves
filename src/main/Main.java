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

	/**
	 * @param args
	 */
	public static void main(String[] args) {

        ServerGUI serv = new ServerGUI(1337);
        ClientGUI cli = new ClientGUI("Alice", 1337);
        ClientGUI cli2 = new ClientGUI("Bob", 1337);
	}

}
