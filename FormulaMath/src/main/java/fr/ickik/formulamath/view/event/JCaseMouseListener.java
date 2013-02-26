package fr.ickik.formulamath.view.event;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JRadioButton;

import fr.ickik.formulamath.view.JCase;

/**
 * Define a listener for {@link JCase} and {@link JRadioButton} to display
 * the selected case in the map and highlight it in the menu.
 * @author Ickik
 * @version 0.1.000, 18th February 2013.
 * @since 0.3.10
 */
public final class JCaseMouseListener implements MouseListener {

	private final Color color;
	private final JRadioButton radioButton;
	private final JCase jCase;
	
	public JCaseMouseListener(final JCase jCase, final JRadioButton radioButton) {
		this.jCase = jCase;
		this.radioButton = radioButton;
		this.color = radioButton.getForeground();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		jCase.getModel().setBackgroundColor(Color.WHITE);
		radioButton.setForeground(Color.RED);
		jCase.repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		jCase.getModel().setBackgroundColor(null);
		radioButton.setForeground(color);
		jCase.repaint();
	}

}
