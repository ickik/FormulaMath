package fr.ickik.formulamath.controler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.ChuckNorrisTimer;
import fr.ickik.formulamath.model.map.MapDimension;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.MapManagerConstructor;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;
import fr.ickik.formulamath.view.ConfigurationFrame;
import fr.ickik.formulamath.view.MainFrame;

/**
 * Controller of the application in MVC design pattern. It receive event from the view to
 * transmit them to the appropriate model if needed.
 * @author Ickik
 * @version 0.1.001, 3 mai 2012
 * @since 0.2
 */
public final class FormulaMathController {

	private final PlayerManager playerManager;
	private final MapManager mapManager;
	private final ConfigurationFrame configurationFrame;
	private final MainFrame mainFrame;
	private ExecutorCompletionService<MapManager> completion;
	
	private static final Logger log = LoggerFactory.getLogger(FormulaMathController.class);
	
	public FormulaMathController(PlayerManager playerManager, MapManager mapManager) {
		this.playerManager = playerManager;
		this.mapManager = mapManager;
		configurationFrame = new ConfigurationFrame(this);
		mainFrame = new MainFrame(playerManager, mapManager, this);
	}
	
	public void initManager(int size) {
		log.debug("initialization of the map manager with a dimension of {} case", size);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		completion = new ExecutorCompletionService<MapManager>(executor);
		completion.submit(new MapManagerConstructor(mapManager, size));
		executor.shutdown();
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

	public void addPlayer(PlayerType type, String name) {
		playerManager.addPlayer(new Player(type, name));
	}

	public void closeConfigurationFrame() {
		log.debug("Close configuration frame");
		configurationFrame.close();
		try {
			log.trace("Wait completion service");
			completion.take();
		} catch (InterruptedException e) {
			log.warn("ExecutorService has been interrupted : {}", e.getMessage());
			mapManager.init(MapDimension.MEDIUM.getValue());
			mapManager.constructRoad();
		}
		log.debug(mapManager.toString());
		log.debug(mapManager.getRoadDirectionInformationList().toString());
		log.debug("creation of main frame");
		mainFrame.display();
		ChuckNorrisTimer.getInstance().addChuckNorrisListener(mainFrame);
	}
}
