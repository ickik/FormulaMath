package fr.ickik.formulamath.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.Field;

/**
 * Panel creation class. It creates the panel for Human player to choose the
 * start position on the starting line.
 * @author Ickik
 * @version 0.1.003, 11 June 2012
 * @since 0.3
 */
public final class PlayVectorChooserPanel {

	private final JPanel panel;
	private final JButton playButton;
	private final FormulaMathController controller;
	private List<List<JCase>> caseArrayList;
	private static final Logger log = LoggerFactory.getLogger(PlayVectorChooserPanel.class);
	
	/**
	 * Constructor that initializes the variables to construct the panel to display the
	 * vectors to the human player.
	 * @param panel the panel to use to display the vectors.
	 * @param playButton the button to validate the choice of one vector. It is used to
	 * define a new listener.
	 * @param controller the controller of the application.
	 */
	public PlayVectorChooserPanel(JPanel panel, JButton playButton, FormulaMathController controller) {
		this.panel = panel;
		this.playButton = playButton;
		this.controller = controller;
	}
	
	/**
	 * Construct the panel and display them into the main frame of the application.
	 * @param player the player for which display the possibilities.
	 * @param vectorList the list of possibilities.
	 * @param mapSize the size of the map.
	 * @param caseArrayList the list of {@link JCase} displayed in the frame graphic, representation of the map.
	 */
	public void construct(final Player player, final List<Vector> vectorList, final int mapSize, final List<List<JCase>> caseArrayList) {
		panel.removeAll();
		this.caseArrayList = caseArrayList;
		ButtonGroup group = new ButtonGroup();

		int distance = (caseArrayList.size() - mapSize) / 2;
		int xTrayPanel = player.getPosition().getX() + distance;
		int yTrayPanel =player.getPosition().getY() + distance;
		log.trace("Player position :{}", player.getPosition().toString());
		log.trace("Player position on map : ( {}, {} )", xTrayPanel, yTrayPanel);
		for (int i = 0; i < vectorList.size();) {
			Vector v = vectorList.get(i);
			JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
			int y = getCoordinateLimit(yTrayPanel - v.getY(), distance);
			int x = getCoordinateLimit(xTrayPanel + v.getX(), distance);
			log.debug("solution : {}", vectorList.get(i).toString());
			log.trace("Player final  position on map : ( {}, {} )", x, y);

			JCase c2 = caseArrayList.get(y).get(x);
			Line2D line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
			log.trace("Vector's line on map from ( {}, {} ) to ( {} , {} )", new Object[]{c.getX(), c.getY(), c2.getX(), c2.getY()});
			if (isGrassIntersection(line)) {
				log.trace("{} intersects grass", vectorList.get(i).toString());
				vectorList.remove(i);
			} else {
				i++;
			}
		}
		if (vectorList.isEmpty()) {
			log.debug("The current human player loses, call controller.play(null)");
			JOptionPane.showMessageDialog(panel, "No possibilities to play, you lose!", AbstractFormulaMathFrame.getTitle(), JOptionPane.INFORMATION_MESSAGE);
			controller.play(null);
			return;
		}
		panel.setLayout(new GridLayout(vectorList.size(), 1));
		final JRadioButton[] solution = new JRadioButton[vectorList.size()];
		for (int i = 0; i < vectorList.size(); i++) {
			JRadioButton box = new JRadioButton("( " + Integer.toString(vectorList.get(i).getX()) + " , " + Integer.toString(vectorList.get(i).getY()) + " )");
			box.setEnabled(true);
			box.setSelected(false);
			group.add(box);
			solution[i] = box;
			panel.add(box);
		}
		panel.validate();
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selected = getSelectedButton(solution);
				if (selected == -1) {
					return;
				}
				Vector vector = vectorList.get(selected);
				log.trace("Play button pushed, checkbox selected : {}", selected);
				log.debug("Vector selected : {}", vector.toString());
				for (JToggleButton button : solution) {
					button.setEnabled(false);
				}
				log.trace("checkbox disabled!");//peut etre mettre le test de fin de ligne dans le model
				int distance = (caseArrayList.size() - mapSize) / 2;
				int xTrayPanel = player.getPosition().getX() + distance;
				int yTrayPanel = player.getPosition().getY() + distance;
				log.trace("Player's position in the map ({},{})", xTrayPanel, yTrayPanel);
				JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
				int x = getCoordinateLimit(xTrayPanel + vector.getX(), mapSize + distance);
				int y = getCoordinateLimit(yTrayPanel - vector.getY(), mapSize + distance);
				log.trace("Player's new position in the map ({},{})", x, y);
				
				JCase c2 = caseArrayList.get(y).get(x);
				log.trace("Case1 ({}, {})", c.getX(), c.getY());
				log.trace("Case1 ({}, {})", c2.getX(), c2.getY());
				Line2D line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));

				if (isEndLineIntersection(line)) {
					log.debug("End line intersection, lastPlay is called");
					controller.lastPlay(vector);
				} else {
					log.debug("No intersection playing normally");
					controller.play(vector);
				}
				playButton.removeActionListener(this);
			}
		});
	}
	
	private int getCoordinateLimit(int coordinate, int mapSize) {
		if (coordinate < 0) {
			return 0;
		} else if (coordinate >= mapSize) {
			return mapSize - 1;
		}
		return coordinate;
	}
	
	private int getSelectedButton(JToggleButton[] buttonArray) {
		int len = buttonArray.length;
		for (int i = 0; i < len; i++) {
			if (buttonArray[i].isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean isGrassIntersection(Line2D shape) {
		log.debug("call isGrassIntersection method");
		return checkIntersection(shape, Field.GRASS);
	}
	
	private boolean isEndLineIntersection(Line2D shape) {
		log.debug("isEndLineIntersection {} {}", shape.getBounds().getLocation().toString(), shape.getBounds().getSize().toString());
		return checkIntersection(shape, Field.FINISHING_LINE);
	}
	
	private boolean checkIntersection(Line2D shape, Field terrain) {
		for (List<JCase> caseList : caseArrayList) {
			for (JCase c : caseList) {
				if (c.getModel() != null && c.getModel().getField() == terrain) {
					//log.debug("Case ({}, {}) size ({}, {}) verify ({}, {}) size ({}, {})", new Object[]{c.getX(), c.getY(), c.getWidth(), c.getHeight(), shape.getBounds2D().getX(), shape.getBounds2D().getY(),shape.getBounds2D().getWidth(), shape.getBounds2D().getHeight() });
					//log.trace("shape.intersectsLine() = {}", shape.intersectsLine(c.getX(), c.getY(), c.getX() + c.getWidth(), c.getY() + c.getHeight()));
					//log.trace("shape.intersectsLine2() = {}", shape.intersectsLine(c.getX() + c.getWidth(), c.getY(), c.getX(), c.getY() + c.getHeight()));
					boolean result1 =shape.intersectsLine(c.getX(), c.getY(), c.getX() + c.getWidth(), c.getY() + c.getHeight());
					boolean result2 = shape.intersectsLine(c.getX() + c.getWidth(), c.getY(), c.getX(), c.getY() + c.getHeight());
					if (result1 || result2) {
						log.info("intersection found for shape and {} on ( {}, {} )", new Object[]{terrain, c.getX(), c.getY()});
						return true;
					}
				}
			}
		}
		return false;
	}
}
