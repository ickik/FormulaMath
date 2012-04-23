package fr.ickik.formulamath.view;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.ickik.formulamath.model.FormulaMathProperty;
import fr.ickik.formulamath.model.PropertiesModel;

/**
 * Abstract which define the default behavior of frames.
 * @author Ickik
 * @version 0.1.003, 23 apr. 2012
 */
public abstract class AbstractFormulaMathFrame {

	public static final String NAME = "FormulaMath";
	public static final String VERSION = "0.2";
	
	/**
	 * Default constructor of the frame.
	 */
	public AbstractFormulaMathFrame() {
	}
	
	String getTitle() {
		return NAME + " " + VERSION;
	}
	
	List<Image> getIconList() {
		//ClassLoader cl = this.getClass().getClassLoader();
		//Icon saveIcon  = new ImageIcon(cl.getResource("images/save.gif"));
		//Icon cutIcon   = new ImageIcon(cl.getResource("images/cut.gif"));
		ImageIcon icon = new ImageIcon(ConfigurationFrame.class.getResource("img/FormulaMath_icon.png"));
		return Arrays.asList(icon.getImage());
	}
	
	/**
	 * Display a Dialog message box with an error logo.
	 * @param msg the message to display.
	 */
	void displayErrorMessage(String msg, JFrame frame) {
		JOptionPane.showMessageDialog(frame, msg, MainFrame.getTitle() + " - ERROR!", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Display a Dialog message box with an information logo.
	 * @param msg the message to display.
	 */
	void displayMessage(String msg, JFrame frame) {
		JOptionPane.showMessageDialog(frame, msg, MainFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Centered the JFrame in the screen.
	 */
	void centeredFrame(JFrame frame) {
		double w = frame.getWidth();
		double h = frame.getHeight();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));
	}
	
	/**
	 * Save the version in the .property file. So if the user modifies the file,
	 * the update will already functional.
	 */
	void saveVersion() {
		PropertiesModel.getSingleton().putDefaultProperty(FormulaMathProperty.VERSION);
		try {
			PropertiesModel.getSingleton().save();
		} catch (IOException e) {
			
		}
	}
}
