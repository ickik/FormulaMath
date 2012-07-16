package fr.ickik.formulamath.view;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Abstract which define the default behavior of frames. This abstract class
 * contains common function for views.
 * @author Ickik
 * @version 0.1.008, 13 June 2012
 */
public abstract class AbstractFormulaMathFrame {

	public static final String NAME = "FormulaMath";
	public static final String VERSION = "0.3.8";
	private final JFrame frame;
	
	/**
	 * Default constructor of the frame.
	 */
	public AbstractFormulaMathFrame() {
		this.frame = new JFrame(getTitle());
	}
	
	JFrame getFrame() {
		return frame;
	}

	public static String getTitle() {
		return NAME + " " + VERSION;
	}
	
	public void close() {
		frame.dispose();
	}
	
	void displayFrame() {
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setIconImages(getIconList());
		centeredFrame();
		frame.setVisible(true);
	}
	
	private List<Image> getIconList() {
		ImageIcon icon = new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/FormulaMath_icon.png"));
		ImageIcon bigIcon = new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/FormulaMath_big_icon.png"));
		return Arrays.asList(icon.getImage(), bigIcon.getImage());
	}
	
	/**
	 * Display a Dialog message box with an error logo.
	 * @param msg the message to display.
	 */
	void displayErrorMessage(String msg) {
		JOptionPane.showMessageDialog(frame, msg, getTitle() + " - ERROR!", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display a Dialog message box with an information logo.
	 * @param msg the message to display.
	 */
	void displayMessage(String msg) {
		JOptionPane.showMessageDialog(frame, msg, getTitle(), JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Centered the JFrame in the screen.
	 */
	private void centeredFrame() {
		//if (frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			GraphicsConfiguration gconf = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().getDefaultConfiguration();
	
			Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gconf);
			int w = frame.getWidth();
			int h = frame.getHeight();
			double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth() - insets.left - insets.right - 5;
			double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - insets.top - insets.bottom - 5;
			//frame.setSize(w, h);
			frame.setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));
		//}
	}
	
	public void disable() {
		frame.setEnabled(false);
	}
	
	public void enable() {
		frame.setEnabled(true);
		frame.toFront();
	}
	
	public void updateLnF(String className) {
		SwingUtilities.updateComponentTreeUI(frame);
	}
}
