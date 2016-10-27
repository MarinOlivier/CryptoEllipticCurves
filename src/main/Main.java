/**
 * 
 */
package main;

import curves.Curve;
import gui.ClientGUI;
import gui.ServerGUI;

/**
 * @author root
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        Curve C = new Curve("cw512", "w512-001.gp");
        ServerGUI serv = new ServerGUI(1337);
        ClientGUI cli = new ClientGUI("Alice");
        //ClientGUI cli2 = new ClientGUI();
	}

}
