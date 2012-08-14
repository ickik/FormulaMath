package fr.ickik.formulamath.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.Field;

/**
 * This class create a JPanel which is displayed to give a the first move on map for a human player.
 * @author Ickik
 * @version 0.1.002, 17 July 2012
 * @since 0.3
 */
public final class FirstMovePanel {

	private final JPanel panel;
	private final JButton playButton;
	private final FormulaMathController controller;
	private final JFrame frame;
	private List<List<JCase>> caseArrayList;
	private static final Logger log = LoggerFactory.getLogger(StartPositionChooserPanel.class);
	
	public FirstMovePanel(JPanel panel, JButton playButton, FormulaMathController controller, JFrame frame) {
		this.panel = panel;
		this.frame = frame;
		this.playButton = playButton;
		this.controller = controller;
	}
	
	public void construct(final List<List<JCase>> caseArrayList, final Position position, final int mapSize) {
		this.caseArrayList = caseArrayList;
		panel.removeAll();
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("x"));
		final JTextField xField = new JTextField();
		xField.addKeyListener(getKeyListener(xField));
		panel.add(xField);
		panel.add(new JLabel("y"));
		final JTextField yField = new JTextField();
		yField.addKeyListener(getKeyListener(yField));
		panel.add(yField);
		panel.validate();
		removeActionListener();
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				log.trace("First step action");
				if (!checkFirstMovingValues(xField, yField)) {
					displayMessage("Values not correct");
					return;
				}
				log.trace("values entered in the both textfield are correct");
				int distance = MainFrame.MAP_MARGIN / 2;
				int xMoving = getValue(xField);
				int yMoving = getValue(yField);
				log.debug("Vector ({}, {})", xMoving, yMoving);
				int xTrayPanel = position.getX() + distance;
				int yTrayPanel = position.getY() + distance;
				log.debug("Player position on grid ({}, {})", xTrayPanel, yTrayPanel);
				JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
				log.debug("Test futur position on grid ({}, {})", (xTrayPanel + xMoving), (yTrayPanel - yMoving));
				JCase c2 = caseArrayList.get(yTrayPanel - yMoving).get(xTrayPanel + xMoving);
				if (c2.getModel() != null) {
					log.debug("Futur position is occuped : {}", c2.getModel().isOccuped());
					if (c2.getModel().isOccuped()) {
						log.warn("Player {} on this case", c2.getModel().getIdPlayer());
						displayMessage("Player on it");
						return;
					}
				}
				
				Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
				if (isGrassIntersection(line)) {
					log.warn("The move intersect grass field");
					displayMessage("Your are in grass!!!!");
					return;
				}
				log.trace("The player can move");
				controller.firstMove(new Vector(xMoving, yMoving));
				playButton.removeActionListener(this);
			}
		
			/**
			 * Display a Dialog message box with an information logo.
			 * @param msg the message to display.
			 */
			private void displayMessage(String msg) {
				JOptionPane.showMessageDialog(frame, msg, AbstractFormulaMathFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	private void removeActionListener() {
		for (ActionListener listener : playButton.getActionListeners()) {
			playButton.removeActionListener(listener);
		}
	}
	
	private KeyListener getKeyListener(final JTextField textField) {
		return new KeyListener() {

			private final Pattern pattern = Pattern.compile("-{0,1}[\\d]+");

			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (pattern.matcher(textField.getText()).matches()) {
					textField.setForeground(Color.GREEN);
				} else {
					textField.setForeground(Color.RED);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {}
		};	
	}
	
	private boolean checkFirstMovingValues(JTextField xTextField, JTextField yTextField) {
		Pattern pattern = Pattern.compile("-{0,1}[\\d]+");
		Matcher xMatcher = pattern.matcher(xTextField.getText());
		Matcher yMatcher = pattern.matcher(yTextField.getText());
		return xMatcher.matches() && yMatcher.matches();
	}
	
	private int getValue(JTextField xTextField) {
		if ("".equals(xTextField)) {
			return 0;
		}
		return Integer.parseInt(xTextField.getText());
	}
	
	private boolean isGrassIntersection(Shape shape) {
		log.debug("isGrassIntersection");
		return checkIntersection(shape, Field.GRASS);
	}
	
	private boolean checkIntersection(Shape shape, Field terrain) {
		for (List<JCase> caseList : caseArrayList) {
			for (JCase c : caseList) {
				if (c.getModel() != null && c.getModel().getField() == terrain) {
//					if (shape.intersects(c.getX() + 1, c.getY() + 1, c.getWidth() - 1, c.getHeight() - 1)) {
					if (shape.intersects(c.getX() + 1, c.getY() + 1, c.getWidth() - 1, c.getHeight() - 1)) {
						log.debug("intersection for shape and {} is {}", terrain, true);
						return true;
					}
				}
			}
		}
		log.debug("intersection for shape and {} is {}", terrain, false);
		return false;
	}
}
