package fr.ickik.formulamath.model;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.view.MainFrame;

/**
 * Class which manages the timer that display a chuck norris fact every minutes (default) in
 * the title of the first frame given in argument.
 * @author Ickik
 * @version 0.1.000, 16 apr. 2012
 */
public final class ChuckNorrisTimer {

	private static ChuckNorrisTimer chuckTimer;
	private final Timer timer;
	private final JFrame frame;
	
	private ChuckNorrisTimer(final JFrame mainFrame) {
		frame = mainFrame;
		if (PropertiesModel.getSingleton().getProperty(FormulaMathProperty.CHUCK_NORRIS_TIME).isEmpty()) {
			PropertiesModel.getSingleton().putDefaultProperty(FormulaMathProperty.CHUCK_NORRIS_TIME);
		}
		timer = new Timer(Integer.parseInt(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.CHUCK_NORRIS_TIME)) * 60000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setTitle(MainFrame.NAME + " " + MainFrame.VERSION + " - " + ChuckNorrisSingleton.getInstance().getRandomFact());
				frame.validate();
			}
		});
		if (Boolean.getBoolean(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.CHUCK_NORRIS_ACTIVATE))) {
			start();
		} else {
			stop();
		}
	}

	public static ChuckNorrisTimer getInstance(JFrame mainFrame) {
		if (chuckTimer == null) {
			chuckTimer = new ChuckNorrisTimer(mainFrame);
		}
		return chuckTimer;
	}
	
	public static ChuckNorrisTimer getInstance() throws FormulaMathException {
		if (chuckTimer == null) {
			throw new FormulaMathException("Illegal state of the timer. You should instantiate with a frame the first time");
		}
		return chuckTimer;
	}
	
	public void start() {
		frame.setTitle(MainFrame.NAME + " " + MainFrame.VERSION + " - " + ChuckNorrisSingleton.getInstance().getRandomFact());
		frame.validate();
		timer.start();
	}
	
	public void stop() {
		frame.setTitle(MainFrame.NAME + " " + MainFrame.VERSION);
		frame.validate();
		timer.stop();
	}
	
	public boolean isRunning() {
		return timer.isRunning();
	}
}
