package fr.ickik.formulamath.controler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;
import fr.ickik.formulamath.view.ConfigurationFrame;
import fr.ickik.formulamath.view.MainFrame;

/**
 * Controler of the application in MVC design pattern. It receive event from the view to
 * transmit them to the appropriate model if needed.
 * @author Ickik
 * @version 0.1.000, 25 apr. 2012
 * @since 0.2
 */
public final class FormulaMathController {

	private final PlayerManager playerManager;
	private final MapManager mapManager;
	private final ConfigurationFrame configurationFrame;
	private MainFrame mainFrame;
	//private final MainFrame mainFrame;
	private static final Logger log = LoggerFactory.getLogger(FormulaMathController.class);
	
	public FormulaMathController(PlayerManager playerManager, MapManager mapManager) {
		this.playerManager = playerManager;
		this.mapManager = mapManager;
		configurationFrame = new ConfigurationFrame(this);
		//mainFrame = new MainFrame(playerManager, mapManager);
	}
	
	public void initManager(int size) {
		/*ExecutorService executor = Executors.newSingleThreadExecutor();
		completion = new ExecutorCompletionService<MapManager>(executor);
		completion.submit(new MapManagerConstructor());
		executor.shutdown();*/
		mapManager.init(size);
		mapManager.constructRoad();
		/*MapManager map = null;
		try {
			map = completion.take().get();
		} catch (InterruptedException e) {
			log.error("ExecutorService has been interrupted : {}", e.getMessage());
		} catch (ExecutionException e) {
			log.error("ExecutorService encount an exception during execution : {}", e.getMessage());
		} finally {
			map = new MapManager(100);
			map.init();
			map.constructRoad();
		}*/
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
		configurationFrame.close();
		mainFrame = new MainFrame(playerManager, mapManager, this);
	}
}
