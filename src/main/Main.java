/**
 * 
 */
package main;

import curves.Curve;
import gui.ClientGUI;
import gui.ServerGUI;

import java.math.BigDecimal;
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
        C = new Curve("cw512", "w512-001.gp");
		C.setGx(new BigDecimal(3, MathContext.DECIMAL64));
		C.setGy(new BigDecimal(4, MathContext.DECIMAL64));
        ServerGUI serv = new ServerGUI(1337);
        ClientGUI cli = new ClientGUI("Alice");
        //ClientGUI cli2 = new ClientGUI();
	}

}
