package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Toolkit;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import fr.ickik.formulamath.model.ChuckNorrisTimer;
import fr.ickik.formulamath.model.FormulaMathProperty;
import fr.ickik.formulamath.model.PropertiesModel;

/**
 * This class only get an instance of the AboutServer. The about
 * frame resume the application (title, version, author, contact).
 * @author Ickik.
 * @version 0.1.002, 22 mar. 2012.
 */
public final class AboutFrame {

	private final JFrame mainFrame;
	private final JFrame frame;
	
	private AboutFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
		mainFrame.setEnabled(false);
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.add(createPanel(), BorderLayout.CENTER);
		frame.add(createBellowPanel(), BorderLayout.SOUTH);
		frame.pack();
		centeredFrame(frame);
		frame.setVisible(true);
	}
	
	/**
	 * Return an instance of the About frame.
	 * @return an instance of AboutFrame.
	 */
	public static AboutFrame getNewInstance(JFrame mainFrame) {
		return new AboutFrame(mainFrame);
	}
	
	private JPanel createPanel() {
		JPanel panel = new JPanel(new GridLayout(4,2));
		panel.add(new JLabel(MainFrame.getTitle()));
		JLabel label = new JLabel("Developed by Ickik");
		label.addMouseListener(getMouseListener());
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
		frame.getRootPane().setDefaultButton(okButton);
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
	
	private MouseListener getMouseListener() {
		return new MouseListener() {
			private boolean selected = ChuckNorrisTimer.getInstance(null).isRunning();
			
			@Override
			public void mouseClicked(MouseEvent e) {
				selected = !selected;
				if (selected) {
					ChuckNorrisTimer.getInstance(null).start();
				} else {
					ChuckNorrisTimer.getInstance(null).stop();
				}
				PropertiesModel.getInstance().put(FormulaMathProperty.CHUCK_NORRIS_ACTIVATE, Boolean.toString(selected));
				mainFrame.repaint();
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
	
	private void centeredFrame(JFrame frame) {
		double w = frame.getWidth();
		double h = frame.getHeight();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));
	}
	
	private JButton createOKButton() {
		JButton okButton = new JButton("OK");
		okButton.setMnemonic(KeyEvent.VK_O);
		frame.getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		return okButton;
	}
	
	private void quit() {
		mainFrame.setEnabled(true);
		frame.dispose();
	}
}
