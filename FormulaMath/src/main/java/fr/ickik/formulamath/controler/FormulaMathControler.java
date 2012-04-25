package fr.ickik.formulamath.controler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.view.MainFrame;

/**
 * Controler of the application in MVC design pattern. It receive event from the view to
 * transmit them to the appropriate model if needed.
 * @author Ickik
 * @version 0.1.000, 1 mai 2012
 */
public final class FormulaMathControler {

	//private final PlayerManager playerManager;
	//private final MapManager mapManager;
	//private final MainFrame mainFrame;
	private static final Logger log = LoggerFactory.getLogger(FormulaMathControler.class);
	
	public FormulaMathControler(PlayerManager playerManager, MapManager mapManager) {
		//this.playerManager = playerManager;
		//this.mapManager = mapManager;
		//mainFrame = new MainFrame(playerManager, mapManager);
	}
	
	
	public void openHelpFile() throws FormulaMathException {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(new File("./help.pdf"));
			} catch (IOException e) {
				log.error("Help file not found or corrupted! {}", e.getMessage());
				throw new FormulaMathException(e.getMessage());
			}
		}
	}
}
