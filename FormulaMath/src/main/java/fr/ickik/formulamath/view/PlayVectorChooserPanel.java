package fr.ickik.formulamath.view;

import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.Field;

public class PlayVectorChooserPanel {

	private final JPanel panel;
	private final JButton playButton;
	private final FormulaMathController controller;
	private List<List<JCase>> caseArrayList;
	private static final Logger log = LoggerFactory.getLogger(PlayVectorChooserPanel.class);
	
	public PlayVectorChooserPanel(JPanel panel, JButton playButton, FormulaMathController controller) {
		this.panel = panel;
		this.playButton = playButton;
		this.controller = controller;
	}
	
	public void construct(final Player player, final List<Vector> vectorList, final int mapSize,final List<List<JCase>> caseArrayList) {
		panel.removeAll();
		this.caseArrayList = caseArrayList;
		panel.setLayout(new GridLayout(vectorList.size(), 1));
		final JRadioButton[] solution = new JRadioButton[vectorList.size()];
		ButtonGroup group = new ButtonGroup();
		
		int distance = (caseArrayList.size() - mapSize) / 2;
		int xTrayPanel = player.getPosition().getX() + distance;
		int yTrayPanel =player.getPosition().getY() + distance;
		for (int i = 0; i < vectorList.size(); i++) {
			Vector v = vectorList.get(i);
			JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
			JCase c2 = caseArrayList.get(yTrayPanel - v.getY()).get(xTrayPanel + v.getX());
			Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
			
			log.debug("solution : {}", vectorList.get(i).toString());
			if (isGrassIntersection(line)) {
				vectorList.remove(i);
			}
		}
		
		for (int i = 0; i < vectorList.size(); i++) {
			JRadioButton box = new JRadioButton("( " + Integer.toString(vectorList.get(i).getX()) + " , " + Integer.toString(vectorList.get(i).getY() - 1) + " )");
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
				for (JToggleButton button : solution) {
					button.setEnabled(false);
				}
				log.trace("checkbox disabled!");//peut etre mettre le test de fin de ligne dans le model
				int distance = (caseArrayList.size() - mapSize) / 2;
				int xTrayPanel = player.getPosition().getX() + distance;
				int yTrayPanel =player.getPosition().getY() + distance;
				JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
				JCase c2 = caseArrayList.get(yTrayPanel - vector.getY()).get(xTrayPanel + vector.getX());
				Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
				
				if (isEndLineIntersection(line)) {
					controller.lastPlay(vector);
				} else {
					controller.play(vector);
				}
				playButton.removeActionListener(this);
			}
		});
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
	
	private boolean isGrassIntersection(Shape shape) {
		log.debug("isGrassIntersection");
		return checkIntersection(shape, Field.GRASS);
	}
	
	private boolean isEndLineIntersection(Shape shape) {
		log.debug("isEndLineIntersection {} {}", shape.getBounds().getLocation(), shape.getBounds().getSize());
		return checkIntersection(shape, Field.FINISHING_LINE);
	}
	
	private boolean checkIntersection(Shape shape, Field terrain) {
		for (List<JCase> caseList : caseArrayList) {
			for (JCase c : caseList) {
				if (c.getModel() != null && c.getModel().getField() == terrain) {
					if (shape.intersects(c.getX(), c.getY(), c.getWidth(), c.getHeight())) {
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
