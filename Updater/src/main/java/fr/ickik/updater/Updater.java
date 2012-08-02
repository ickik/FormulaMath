package fr.ickik.updater;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.updater.model.UpdateModel;

/**
 * Main class which contains the main static method.
 * @author Ickik
 * @version 0.1.002, 31 mai 2012
 */
public final class Updater {

	public static final String VERSION = "1.0.1";
	private static final Logger logger = LoggerFactory.getLogger(Updater.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup executionThreadGroup = new ExecutionThreadGroup();
		new Thread(executionThreadGroup, "") {
			public void run() {
				logger.debug("Start Updater application");
				if (!checkVersion()) {
					JOptionPane.showMessageDialog(null, "Java version not compatible please update on www.java.com", "ERROR!", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				UpdateModel model = new UpdateModel();
				if (model.isConnectionAvailable()) {
					UpdateFrame main = new UpdateFrame(model);
					logger.debug("Network connection found, check version availalble");
					if (model.isUpdateAvailable()) {
						logger.debug("Trying to download the new version");
						main.setVisible(true);
						model.update();
					} else {
						logger.debug("No new version found, starting application");
						model.startApplication();
					}
				} else {
					logger.debug("No network connection found, starting application");
					model.startApplication();
				}
			}
		}.start();
	}
	
	private static boolean checkVersion() {
		String version = System.getProperty("java.version");
		logger.debug("Current java version : {}", version);
		if (version.matches("1\\.[6-7].*")) {
			return true;
		}
		return false;
	}

}
