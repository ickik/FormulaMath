package fr.ickik.formulamath;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public final class JCase extends JPanel {

	public JCase() {
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isOpaque()) {
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth(), getHeight());
		}
	}
}
