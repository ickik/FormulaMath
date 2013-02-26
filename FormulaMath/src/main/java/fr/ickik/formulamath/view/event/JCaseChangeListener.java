package fr.ickik.formulamath.view.event;

import java.awt.Color;

import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.view.JCase;

/**
 * Define a listener for {@link JCase} and {@link JRadioButton} to display
 * the selected case in the map and highlight it in the menu.
 * @author Ickik
 * @version 0.1.000, 21th February 2013.
 * @since 0.3.10
 */
public class JCaseChangeListener implements ChangeListener {

	private final Color color;
	private final JRadioButton radioButton;
	private final JCase jCase;
	private final Player player;
	
	public JCaseChangeListener(final JCase jCase, final JRadioButton radioButton, final Player player) {
		this.jCase = jCase;
		this.radioButton = radioButton;
		this.color = radioButton.getForeground();
		this.player = player;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (radioButton.isSelected()) {
			jCase.getModel().setBackgroundColor(Color.WHITE);
			radioButton.setForeground(Color.RED);
			jCase.repaint();
		} else if (!radioButton.isSelected()) {
			jCase.getModel().setBackgroundColor(null);
			radioButton.setForeground(color);
			jCase.repaint();
		}
	}

}
