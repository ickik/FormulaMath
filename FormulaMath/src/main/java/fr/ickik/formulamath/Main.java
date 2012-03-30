package fr.ickik.formulamath;

import fr.ickik.formulamath.view.ConfigurationFrame;

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
