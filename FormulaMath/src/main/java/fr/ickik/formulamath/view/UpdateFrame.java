package fr.ickik.formulamath.view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import fr.ickik.formulamath.update.UpdateModel;
import fr.ickik.formulamath.update.UpdaterListener;

/**
 * Frame displayed when an update is available. This frame is composed by a progress bar
 * that evolves with the download.
 * @author Patrick Allgeyer
 * @version 0.1.000, 23 mar. 2012
 */
public final class UpdateFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JProgressBar bar = new JProgressBar();
	private static final int SLEEP_TIME = 2000;
	
	/**
	 * Constructor of this frame, it needs the {@link UpdateModel} to be
	 * construted.
	 * @param model the update model.
	 */
	public UpdateFrame(UpdateModel model) {
		super();
		final int xDimension = 200;
		final int yDimension = 100;
		add(bar);
		bar.setStringPainted(true);
		setSize(new Dimension(xDimension,yDimension));
		setUndecorated(true);
		model.addUpdateListener(getUpdaterListener());
	}
	
	private UpdaterListener getUpdaterListener() {
		return new UpdaterListener() {

			public void updateValue(int value, String msg) {
				bar.setValue(value);
				bar.setString(msg);
			}

			public void start() {
				bar.setString("The server will start");
				waitDispose();
				new ConfigurationFrame();
			}

			public void restart() {
				bar.setString("The application will restart");
				waitDispose();
			}
		};
	}
	
	private void waitDispose() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
		} finally {
			dispose();
		}
	}
}
