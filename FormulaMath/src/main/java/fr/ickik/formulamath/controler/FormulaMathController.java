package fr.ickik.formulamath.controler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.ChuckNorrisTimer;
import fr.ickik.formulamath.model.FormulaMathProperty;
import fr.ickik.formulamath.model.FormulaMathSaver;
import fr.ickik.formulamath.model.PropertiesModel;
import fr.ickik.formulamath.model.map.MapDimension;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.MapManagerConstructor;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;
import fr.ickik.formulamath.view.AboutFrame;
import fr.ickik.formulamath.view.ConfigurationFrame;
import fr.ickik.formulamath.view.MainFrame;
import fr.ickik.formulamath.view.StatFrame;

/**
 * Controller of the application in MVC design pattern. It receive event from the view to
 * transmit them to the appropriate model if needed.
 * @author Ickik
 * @version 0.1.003, 7 mai 2012
 * @since 0.2
 */
public final class FormulaMathController {

	private final PlayerManager playerManager;
	private final MapManager mapManager;
	private final ConfigurationFrame configurationFrame;
	private final MainFrame mainFrame;
	private final AboutFrame aboutFrame;
	private final StatFrame statFrame;
	private ExecutorCompletionService<MapManager> completion;
	
	private static final Logger log = LoggerFactory.getLogger(FormulaMathController.class);
	
	public FormulaMathController(PlayerManager playerManager, MapManager mapManager) {
		this.playerManager = playerManager;
		this.mapManager = mapManager;
		setLookAndFeel(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.THEME));
		configurationFrame = new ConfigurationFrame(this);
		mainFrame = new MainFrame(playerManager, mapManager, this, PropertiesModel.getSingleton().getProperty(FormulaMathProperty.THEME));
		this.playerManager.addUpdateCaseListener(mainFrame);
		aboutFrame = new AboutFrame(this, ChuckNorrisTimer.getInstance().isRunning());
		statFrame = new StatFrame(this);
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
			log.warn("ExecutorService has been interrupted (or crashed) : {}", e.getMessage());
			log.warn("Creation of default medium size map");
			mapManager.init(MapDimension.MEDIUM.getValue());
			mapManager.constructRoad();
		}
		log.debug(mapManager.toString());
		log.debug(mapManager.getRoadDirectionInformationList().toString());
		log.debug("creation of main frame");
		mainFrame.display(mapManager.getMap());
		ChuckNorrisTimer.getInstance().addChuckNorrisListener(mainFrame);
	}
	
	public void openAboutFrame() {
		mainFrame.disable();
		aboutFrame.display();
	}

	public void closeAboutFrame() {
		mainFrame.enable();
		aboutFrame.close();
	}
	
	public void openStatFrame() {
		mainFrame.disable();
		statFrame.display(playerManager.getPlayerList(), playerManager.getFinishPositionList());
	}
	
	public void closeStatFrame() {
		mainFrame.enable();
		statFrame.close();
	}
	
	public void activateChuckNorrisTimer() {
		log.debug("Activate Chuck Noris Timer");
		ChuckNorrisTimer.getInstance().start();
		PropertiesModel.getSingleton().put(FormulaMathProperty.CHUCK_NORRIS_ACTIVATE, Boolean.toString(true));
	}
	
	public void deactivateChuckNorrisTimer() {
		log.debug("Deactivate Chuck Noris Timer");
		ChuckNorrisTimer.getInstance().stop();
		PropertiesModel.getSingleton().put(FormulaMathProperty.CHUCK_NORRIS_ACTIVATE, Boolean.toString(false));
	}
	
	public void saveProperties() throws FormulaMathException {
		try {
			log.debug("Closing window and saving properties");
			PropertiesModel.getSingleton().putDefaultProperty(FormulaMathProperty.VERSION);
			PropertiesModel.getSingleton().save();
		} catch (IOException e) {
			log.error("Error saving properties for quiting : {} ", e.getMessage());
			throw new FormulaMathException("Error during saving properties file");
		}
	}
	
	public void saveMap(File saveFile) throws IOException {
		/*try {
			FormulaMathSaver.getInstance().saveMap(mapManager, saveFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		FormulaMathSaver.getInstance().saveMap(mapManager, saveFile);
	}
	
	public void loadMap(File loadFile) throws IOException {
		/*try {
			FormulaMathSaver.getInstance().loadMap(loadFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		FormulaMathSaver.getInstance().loadMap(loadFile);
	}
	
	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
			if (mainFrame != null) {
				mainFrame.updateLnF(className);
			}
			if (configurationFrame != null) {
				configurationFrame.updateLnF(className);
			}
			if (aboutFrame != null) {
				aboutFrame.updateLnF(className);
			}
			PropertiesModel.getSingleton().put(FormulaMathProperty.THEME, className);
		} catch (ClassNotFoundException e) {
			log.error("Class {} not found on system : {}", className, e.getMessage());
		} catch (InstantiationException e) {
			log.error("Class {} cannot be instantiate : {}", className, e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("Class {} cannot be acces : {}", className, e.getMessage());
		} catch (UnsupportedLookAndFeelException e) {
			log.error("Look and Feel {} not supported by the system : {}", className, e.getMessage());
		}
	}

}
