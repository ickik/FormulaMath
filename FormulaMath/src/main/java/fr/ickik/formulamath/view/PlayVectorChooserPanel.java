package fr.ickik.formulamath.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
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
import fr.ickik.formulamath.view.event.JCaseMouseListener;

/**
 * Panel creation class. It creates the panel for Human player to choose the
 * start position on the starting line.
 * @author Ickik
 * @version 0.1.009, 21th February 2013.
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

		int marge = MainFrame.MAP_MARGIN / 2;
		int xTrayPanel = player.getPosition().getX() + marge;
		int yTrayPanel = player.getPosition().getY() + marge;
		log.trace("Player position :{}", player.getPosition().toString());
		log.trace("Player position on map : ( {}, {} )", xTrayPanel, yTrayPanel);
		final List<JCase> solutionCaseList = new ArrayList<JCase>();
		final List<MouseListener> mouseListenerList = new ArrayList<MouseListener>();
		//final List<JCaseChangeListener> changeListenerList = new ArrayList<JCaseChangeListener>();
		for (int i = 0; i < vectorList.size();) {
			Vector v = vectorList.get(i);
			JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
			int y = getCoordinateLimit(yTrayPanel - v.getY(), mapSize);
			int x = getCoordinateLimit(xTrayPanel + v.getX(), mapSize);
			log.debug("solution : {}", vectorList.get(i).toString());
			log.trace("Player final  position on map : ( {}, {} )", x, y);

			final JCase c2 = caseArrayList.get(y).get(x);
			Line2D line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
			log.trace("Vector's line on map from ( {}, {} ) to ( {} , {} )", new Object[]{c.getX(), c.getY(), c2.getX(), c2.getY()});
			if (isGrassIntersection(line)) {
				log.trace("{} intersects grass", vectorList.get(i).toString());
				vectorList.remove(i);
			} else {
				i++;
				solutionCaseList.add(c2);
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
			final JRadioButton box = new JRadioButton("( " + Integer.toString(vectorList.get(i).getX()) + " , " + Integer.toString(vectorList.get(i).getY()) + " )");
			box.setEnabled(true);
			box.setSelected(false);
			final JCase cas = solutionCaseList.get(i);
			//JCaseChangeListener listener = new JCaseChangeListener(cas, box, player);
			//box.addChangeListener(listener);
			//changeListenerList.add(listener);
			group.add(box);
			solution[i] = box;
			mouseListenerList.add(initMouseListener(cas, box));
			panel.add(box);
		}
		panel.validate();
		removeActionListener();
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
				//int index = 0;
				for (JToggleButton button : solution) {
					button.setEnabled(false);
					//button.setSelected(false);
					//button.removeChangeListener(changeListenerList.get(index));
					//index++;
				}
				for (int i = 0; i < solutionCaseList.size(); i++) {
					solutionCaseList.get(i).removeMouseListener(mouseListenerList.get(i));
				}
				/*for (JCase jCase : solutionCaseList) {
					MouseListener[] mouseListenerArray = jCase.getMouseListeners();
					jCase.removeMouseListener(mouseListenerArray[0]);
				}*/
				log.trace("checkbox disabled!");//peut etre mettre le test de fin de ligne dans le model
				int marge = MainFrame.MAP_MARGIN / 2;
				int xTrayPanel = player.getPosition().getX() + marge;
				int yTrayPanel = player.getPosition().getY() + marge;
				log.trace("Player's position in the map ({},{})", xTrayPanel, yTrayPanel);
				JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
				int x = getCoordinateLimit(xTrayPanel + vector.getX(), mapSize);
				int y = getCoordinateLimit(yTrayPanel - vector.getY(), mapSize);
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
	
	private void removeActionListener() {
		for (ActionListener listener : playButton.getActionListeners()) {
			playButton.removeActionListener(listener);
		}
	}
	
	private MouseListener initMouseListener(JCase jCase, JRadioButton radioButton) {
		MouseListener listener = new JCaseMouseListener(jCase, radioButton);
		jCase.addMouseListener(listener);
		radioButton.addMouseListener(listener);
		return listener;
	}
	
	private int getCoordinateLimit(int coordinate, int mapSize) {
		if (coordinate < MainFrame.MAP_MARGIN / 2) {
			return MainFrame.MAP_MARGIN / 2;
		} else if (coordinate >= mapSize + (MainFrame.MAP_MARGIN / 2)) {
			return mapSize + (MainFrame.MAP_MARGIN / 2) - 1;
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
					boolean result1 =shape.intersectsLine(c.getX() + 1, c.getY() + 1, c.getX() + c.getWidth() - 1, c.getY() + c.getHeight() - 1);
					boolean result2 = shape.intersectsLine(c.getX() + c.getWidth() - 1, c.getY() + 1, c.getX() + 1, c.getY() + c.getHeight() - 1);
//					boolean result1 =shape.intersectsLine(c.getX() + 1, c.getY() + 1, c.getX() + c.getWidth() - 1, c.getY() + c.getHeight() - 1);
//					boolean result2 = shape.intersectsLine(c.getX() + c.getWidth() + 1, c.getY() - 1, c.getX() + 1, c.getY() + c.getHeight() - 1);
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
