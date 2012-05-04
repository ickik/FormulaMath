package fr.ickik.formulamath.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import fr.ickik.formulamath.controler.ChuckNorrisListener;
import fr.ickik.formulamath.view.AbstractFormulaMathFrame;

/**
 * Class which manages the timer that display a chuck norris fact every minutes (default) in
 * the title of the first frame given in argument.
 * @author Ickik
 * @version 0.1.001, 4 mai 2012
 */
public final class ChuckNorrisTimer {

	private static ChuckNorrisTimer chuckTimer = new  ChuckNorrisTimer();
	private final Timer timer;
	private String title;
	private final List<ChuckNorrisListener> chuckNorrisListenerList = new ArrayList<ChuckNorrisListener>();
	
	private ChuckNorrisTimer() {
		if (PropertiesModel.getSingleton().getProperty(FormulaMathProperty.CHUCK_NORRIS_TIME).isEmpty()) {
			PropertiesModel.getSingleton().putDefaultProperty(FormulaMathProperty.CHUCK_NORRIS_TIME);
		}
		timer = new Timer(Integer.parseInt(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.CHUCK_NORRIS_TIME)) * 60000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateTitle();
			}
		});
		if (Boolean.getBoolean(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.CHUCK_NORRIS_ACTIVATE))) {
			start();
		} else {
			stop();
		}
	}
	
	public void addChuckNorrisListener(ChuckNorrisListener listener) {
		chuckNorrisListenerList.add(listener);
		listener.updateTitle(title);
	}
	
	private void fireUpdateTitle() {
		for (ChuckNorrisListener listener : chuckNorrisListenerList) {
			listener.updateTitle(title);
		}
	}

	public static ChuckNorrisTimer getInstance() {
		return chuckTimer;
	}
	
	private void generateTitle() {
		title = AbstractFormulaMathFrame.getTitle() + " - " + ChuckNorrisSingleton.getInstance().getRandomFact();
		fireUpdateTitle();
	}
	
	public void start() {
		timer.start();
		generateTitle();
	}
	
	public void stop() {
		timer.stop();
	}
	
	public boolean isRunning() {
		return timer.isRunning();
	}
}
