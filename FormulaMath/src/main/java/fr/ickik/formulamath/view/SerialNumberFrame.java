package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.model.JTextFieldLimit;
import fr.ickik.formulamath.model.UserModel;

/**
 * Create a {@link JFrame} to permit the user entering a serial key.
 * @author Ickik
 * @version 0.1.001, 6th September 2012
 * @since 0.3.10
 */
public final class SerialNumberFrame extends AbstractFormulaMathFrame {

	private final FormulaMathController controller;
	private final int textSlotNumber = 5;
	private final JTextField emailTextField = new JTextField(20);
	private final JTextField[] textFieldArray = new JTextField[textSlotNumber];
	private final UserModel model;
	
	public SerialNumberFrame(UserModel model, FormulaMathController controller) {
		this.controller = controller;
		this.model = model;
		getFrame().addWindowListener(createWindowListener());
		
	}
	
	public void display() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getPanel(), BorderLayout.CENTER);
		panel.add(getButtons(), BorderLayout.SOUTH);
		getFrame().add(panel);
		displayFrame();
	}
	
	private WindowListener createWindowListener() {
		return new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				try {
					controller.saveProperties();
				} catch (FormulaMathException e) {
					displayErrorMessage(e.getMessage());
				}
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		};
	}
	
	private JPanel getButtons() {
		JPanel panel = new JPanel(new GridLayout());
		JButton validate = new JButton("Validate");
		validate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				StringBuilder key = new StringBuilder();
				for (int i = 0; i < textSlotNumber; i++) {
					key.append(textFieldArray[i].getText());
					if (i < textSlotNumber - 1) {
						key.append('-');
					}
				}
				if (model.isKeyValide(key.toString())) {
					controller.openConfigurationFrame();
				} else {
					displayErrorMessage("Key invalide");
				}
			}
		});
		
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (JTextField field : textFieldArray) {
					field.setText("");
				}
			}
		});
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		panel.add(validate);
		panel.add(reset);
		panel.add(cancel);
		return panel;
	}
	
	private JPanel getPanel() {
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		
		JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		emailPanel.add(new JLabel("Email :"));
		emailTextField.setText("");
		emailPanel.add(emailTextField);
		emailPanel.add(getRequestKeyButton());
		panel.add(emailPanel);
		JPanel entryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		entryPanel.add(new JLabel("Serial number : "));
		final int textSlotSize = 6;
		for (int i = 0; i < textSlotNumber; i++) {
			JTextField textField = new JTextField(textSlotSize);
			textField.setDocument(new JTextFieldLimit(textSlotSize));
			textFieldArray[i] = textField;
			entryPanel.add(textField);
			if (i < textSlotNumber - 1) {
				entryPanel.add(new JLabel("-"));
			}
		}
		panel.add(entryPanel);
		return panel;
	}
	
	private JButton getRequestKeyButton() {
		JButton button = new JButton("Request Key");
		button.setToolTipText("An email will be sent with a valid key to the given email address");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String email = emailTextField.getText();
				if (isEmailValide(email)) {
					model.keyRequest(email);
					displayMessage("An email will be sent with a valid key to the given email address");
				} else {
					displayErrorMessage("Email address not valide");
				}
			}
			
			private boolean isEmailValide(String emailAddress) {
				Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\\.)+[A-Z]{2,4}$");
				return pattern.matcher(emailAddress).matches();
			}
		});
		return button;
	}
	
}
