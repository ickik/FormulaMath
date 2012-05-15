package fr.ickik.formulamath;

import javax.swing.JOptionPane;

import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.player.PlayerManager;

/**
 * Main class which contains the main static method.
 * @author Ickik
 * @version 0.1.002, 15 mai 2012
 */
public class FormulaMath {

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
				MapManager mapManager = new MapManager();
				PlayerManager playerManager = new PlayerManager(mapManager);
				FormulaMathController controller = new FormulaMathController(playerManager, mapManager);
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
