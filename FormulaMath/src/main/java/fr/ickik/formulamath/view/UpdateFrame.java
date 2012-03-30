package fr.ickik.formulamath.view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import fr.ickik.formulamath.update.UpdateModel;
import fr.ickik.formulamath.update.UpdaterListener;

/**
 * Frame displayed when an update is available. This frame is composed by a progress bar
 * that evolves with the download.
 * @author Patrick Allgeyer
 * @version 0.1.002, 29 mar. 2012
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
	 * constructed.
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
		centeredFrame(this);
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
	
	private void centeredFrame(JFrame frame) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int xCorner = (int) ((dimension.getWidth() - frame.getSize().getWidth()) / 2);
		int yCorner = (int) ((dimension.getHeight() - frame.getSize().getHeight()) / 2);
		frame.setLocation(xCorner, yCorner);
	}
}
