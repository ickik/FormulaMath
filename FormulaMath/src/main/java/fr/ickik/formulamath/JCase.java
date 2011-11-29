package fr.ickik.formulamath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JCase extends JPanel {

	private final Shape shape;
	
	public JCase(int size) {
		setSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
		double sizeDoubleValue = (new Integer(size+1)).doubleValue();
		shape = new Rectangle2D.Double(0.0, 0.0, sizeDoubleValue, sizeDoubleValue);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			g2.draw(shape);
		}
	}
}
