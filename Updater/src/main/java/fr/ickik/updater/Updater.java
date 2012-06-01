package fr.ickik.updater;

import javax.swing.JOptionPane;

/**
 * Main class which contains the main static method.
 * @author Ickik
 * @version 0.1.002, 31 mai 2012
 */
public final class Updater {

	public static final String VERSION = "1.0";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup executionThreadGroup = new ExecutionThreadGroup();
		new Thread(executionThreadGroup, "") {
			public void run() {
				if (!checkVersion()) {
					JOptionPane.showMessageDialog(null, "Java version not compatible please update on www.java.com", "ERROR!", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				UpdateModel model = new UpdateModel();
				if (model.isConnectionAvailable()) {
					UpdateFrame main = new UpdateFrame(model);
					if (model.isUpdateAvailable()) {
						main.setVisible(true);
						model.update();
					} else {
						model.startApplication();
					}
				} else {
					model.startApplication();
				}
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
