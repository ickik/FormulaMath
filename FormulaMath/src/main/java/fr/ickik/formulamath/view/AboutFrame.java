package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.controler.FormulaMathController;

/**
 * This class only get an instance of the AboutServer. The about
 * frame resume the application (title, version, author, contact).
 * @author Ickik.
 * @version 0.1.004, 4 mai 2012.
 */
public final class AboutFrame extends AbstractFormulaMathFrame {

	private final FormulaMathController controller;
	private boolean isChuckNorrisTimerActivated;
	
	public AboutFrame(FormulaMathController controller, boolean isChuckNorrisTimerActivated) {
		this.controller = controller;
		this.isChuckNorrisTimerActivated = isChuckNorrisTimerActivated;
	}
	
	public void display() {
		getFrame().add(createPanel(), BorderLayout.CENTER);
		getFrame().add(createBellowPanel(), BorderLayout.SOUTH);
		displayFrame();
		getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	private JPanel createPanel() {
		JPanel panel = new JPanel(new GridLayout(4,2));
		panel.add(new JLabel(getFrame().getTitle()));
		JLabel label = new JLabel("Developed by Ickik");
		try {
			label.addMouseListener(getMouseListener());
		} catch (FormulaMathException e) {
			e.printStackTrace();
		}
		panel.add(label);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		JLabel mail = new JLabel("<html>(<a href=\"\">patrick.allgeyer</a>)</html>");
		panel.add(mail);
		mail.addMouseListener(getMouseMailAdapter());
		mail.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return panel;
	}
	
	private JPanel createBellowPanel() {
		JPanel buttonPanel = new JPanel();
		JButton okButton = createOKButton();
		getFrame().getRootPane().setDefaultButton(okButton);
		BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
		buttonPanel.setLayout(boxLayout);
		okButton.setAlignmentX(0.5f);
		buttonPanel.add(okButton);
		return buttonPanel;
	}
	
	private MouseAdapter getMouseMailAdapter() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().mail(URI.create("mailto:patrick.allgeyer@gmail.com?subject=Formula Math " + MainFrame.VERSION + ""));
				} catch (IOException e1) {}
			}
		};
	}
	
	private MouseListener getMouseListener() throws FormulaMathException {
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				isChuckNorrisTimerActivated = !isChuckNorrisTimerActivated;
				if (isChuckNorrisTimerActivated) {
					controller.activateChuckNorrisTimer();
				} else {
					controller.deactivateChuckNorrisTimer();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		};
	}
	
	private JButton createOKButton() {
		JButton okButton = new JButton("OK");
		okButton.setMnemonic(KeyEvent.VK_O);
		getFrame().getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.closeAboutFrame();
			}
		});
		return okButton;
	}

}
