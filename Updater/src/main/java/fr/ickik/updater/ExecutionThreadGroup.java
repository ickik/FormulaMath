package fr.ickik.updater;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the thread group class to redefine the behavior when an
 * exception is thrown.
 * @author Ickik
 * @version 0.1.000, 10 mai. 2012
 */
final class ExecutionThreadGroup extends ThreadGroup {

	private static final Logger log = LoggerFactory.getLogger(ExecutionThreadGroup.class);
	
	/**
	 * Constructor of the thread group.
	 */
	public ExecutionThreadGroup() {
		super("ExecutionGroup");
	}

	public void uncaughtException(Thread t, Throwable e) {
		JOptionPane.showMessageDialog(findActiveFrame(), e.toString(), " - Exception Occurred", JOptionPane.ERROR_MESSAGE);
		log.error(e.getMessage());
		log.error("Cause : {}", e.getCause().getMessage());
		e.printStackTrace();
	}

	private Frame findActiveFrame() {
		Frame[] frames = JFrame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame frame = frames[i];
			if (frame.isVisible() || frame.isActive()) {
				return frame;
			}
		}
		return null;
	}
}
