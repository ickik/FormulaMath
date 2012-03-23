package fr.ickik.formulamath.model;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import fr.ickik.formulamath.view.MainFrame;

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
