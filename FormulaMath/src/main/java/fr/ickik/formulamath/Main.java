package fr.ickik.formulamath;

import javax.swing.JOptionPane;

import fr.ickik.formulamath.view.ConfigurationFrame;

/**
 * Main class which contains the main static method.
 * @author Ickik
 * @version 0.1.001, 13 apr. 2012
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup executionThreadGroup = new ExecutionThreadGroup();
		new Thread(executionThreadGroup, "") {
			public void run() {
				if (!checkVersion()) {
					JOptionPane.showMessageDialog(null, "Java version not compatible please update", "ERROR!", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				new ConfigurationFrame();
			}
		}.start();
	}
	
	private static boolean checkVersion() {
		String version = System.getProperty("java.version");
		if (version.matches("1\\.[6-7].*")) {
			return true;
		}
		return false;
	}
}
