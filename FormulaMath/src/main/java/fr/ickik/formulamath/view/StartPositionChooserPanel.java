package fr.ickik.formulamath.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.entity.Position;

/**
 * This class create a JPanel which is displayed to give a start position choice for a human player.
 * @author Ickik
 * @version 0.1.000, 10 mai 2012
 * @since 0.2
 */
public class StartPositionChooserPanel {

	private final JPanel panel;
	private final JButton playButton;
	private final FormulaMathController controller;
	private static final Logger log = LoggerFactory.getLogger(StartPositionChooserPanel.class);
	
	public StartPositionChooserPanel(JPanel panel, JButton playButton, FormulaMathController controller) {
		this.panel = panel;
		this.playButton = playButton;
		this.controller = controller;
	}
	
	public void construct(final List<Position> startPositionList, int mapSize) {
		panel.removeAll();
		panel.setLayout(new GridLayout(startPositionList.size(), 1));
		final JRadioButton[] solution = new JRadioButton[startPositionList.size()];
		ButtonGroup group = new ButtonGroup();
		
		for (int i = 0; i < startPositionList.size(); i++) {
			JRadioButton box = new JRadioButton("( " + Integer.toString(startPositionList.get(i).getX()) + " , " + Integer.toString((mapSize - startPositionList.get(i).getY()) - 1) + " )");
			box.setEnabled(true);
			box.setSelected(false);
			group.add(box);
			solution[i] = box;
			panel.add(box);
		}
		
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = getSelectedButton(solution);
				if (selected == -1) {
					return;
				}
				log.trace("Play button pushed, checkbox selected : {}", selected);
				for (JToggleButton button : solution) {
					button.setEnabled(false);
				}
				log.trace("checkbox disabled!");
				controller.startPosition(startPositionList.get(selected));
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
}
