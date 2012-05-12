package fr.ickik.formulamath;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.view.MainFrame;

/**
 * This class extends the thread group class to redefine the behavior when an
 * exception is thrown.
 * @author Ickik
 * @version 0.1.001, 10 mai. 2012
 */
public class ExecutionThreadGroup extends ThreadGroup {

	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	
	/**
	 * Constructor of the thread group.
	 */
	public ExecutionThreadGroup() {
		super("ExecutionGroup");
	}

	public void uncaughtException(Thread t, Throwable e) {
		JOptionPane.showMessageDialog(findActiveFrame(), e.toString(), MainFrame.getTitle() + " - Exception Occurred", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		log.error(e.getMessage());
		log.error("Cause : {}", e.getCause().getMessage());
		e.printStackTrace();
		//System.exit(1);
	}

	private Frame findActiveFrame() {
		Frame[] frames = JFrame.getFrames();
		for (Frame frame : frames) {
			if (frame.isActive()) {
				return frame;
			}
		}
		for (Frame frame : frames) {
			if (frame.isVisible()) {
				return frame;
			}
		}
		return null;
	}
}
