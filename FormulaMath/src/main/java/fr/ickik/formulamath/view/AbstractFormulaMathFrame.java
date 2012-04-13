package fr.ickik.formulamath.view;

import java.awt.Toolkit;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Abstract which define the default behavior of frames.
 * @author Ickik
 * @version 0.1.001, 13 apr. 2012
 */
public abstract class AbstractFormulaMathFrame extends JFrame {

	private static final long serialVersionUID = -2751870342760395670L;
	public static final String NAME = "FormulaMath";
	public static final String VERSION = "0.1";
	
	/**
	 * Default constructor of the frame. It constructs the frame with
	 * the title defined by concatenation of constants NAME and VERSION.
	 */
	public AbstractFormulaMathFrame() {
		super(NAME + " " + VERSION);
		addIcon();
	}
	
	private void addIcon() {
		ImageIcon icon = new ImageIcon(ConfigurationFrame.class.getResource("img/FormulaMath_icon.png"));
		setIconImages(Arrays.asList(icon.getImage()));
	}
	
	/**
	 * Display a Dialog message box with an error logo.
	 * @param msg the message to display.
	 */
	void displayErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, MainFrame.getTitle() + " - ERROR!", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display a Dialog message box with an information logo.
	 * @param msg the message to display.
	 */
	void displayMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, MainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Centered the JFrame in the screen.
	 */
	void centeredFrame() {
		double w = getWidth();
		double h = getHeight();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));
	}
}
