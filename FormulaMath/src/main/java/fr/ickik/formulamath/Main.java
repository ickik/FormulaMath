package fr.ickik.formulamath;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.ickik.formulamath.view.ConfigurationFrame;
import fr.ickik.formulamath.view.MainFrame;

public class Main extends ThreadGroup {

	public Main() {
		super("ExecutionGroup");
	}

	public void uncaughtException(Thread t, Throwable e) {
		JOptionPane.showMessageDialog(findActiveFrame(), e.toString(), MainFrame.getTitle() + " - Exception Occurred", JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

	private Frame findActiveFrame() {
		Frame[] frames = JFrame.getFrames();
		for (int i = 0; i < frames.length; i++) {
			Frame frame = frames[i];
			if (frame.isVisible()) {
				return frame;
			}
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup main = new Main();
		new Thread(main, "") {
			public void run() {
				new ConfigurationFrame();
			}
		}.start();
	}
}
