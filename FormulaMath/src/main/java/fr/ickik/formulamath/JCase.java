package fr.ickik.formulamath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import fr.ickik.formulamath.model.PlayerManager;

@SuppressWarnings("serial")
public class JCase extends JPanel {

	private final Shape shape;
	private Case model;
	
	public JCase(int size) {
		setOpaque(true);
		Dimension dimension = new Dimension(size, size);
		setSize(dimension);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		double sizeDoubleValue = (new Integer(size+1)).doubleValue();
		shape = new Rectangle2D.Double(0.0, 0.0, sizeDoubleValue, sizeDoubleValue);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			if (model != null) {
				if (model.getIdPlayer() == 0) {
					if (model.getTerrain() == Terrain.END_LINE) {
						setBackground(Color.BLUE);
					} else {
						setBackground(model.getTerrain().getColor());
					}
				} else {
					setBackground(PlayerManager.getInstance().getColorById(model.getIdPlayer()));
				}
			} else {
				setBackground(Color.WHITE);
			}
			g2.setColor(getBackground());
			g2.fill(shape);
			g2.setColor(Color.BLACK);
			g2.draw(shape);
		}
	}
	
	@Override
	public void paintComponents(Graphics g) {
		paint(g);
	}

	public void setModel(Case model) {
		this.model = model;
	}

	public Case getModel() {
		return model;
	}
}
