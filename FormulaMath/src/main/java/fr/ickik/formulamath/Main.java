package fr.ickik.formulamath;

import fr.ickik.formulamath.view.ConfigurationFrame;

/**
 * Main class which contains the main static method.
 * @author Ickik
 * @version 0.1.000, 11 apr. 2012
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup executionThreadGroup = new ExecutionThreadGroup();
		new Thread(executionThreadGroup, "") {
			public void run() {
				new ConfigurationFrame();
			}
		}.start();
	}
}
