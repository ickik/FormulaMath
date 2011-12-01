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
		setSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
		setMaximumSize(new Dimension(size, size));
		setPreferredSize(new Dimension(size, size));
		double sizeDoubleValue = (new Integer(size+1)).doubleValue();
		shape = new Rectangle2D.Double(0.0, 0.0, sizeDoubleValue, sizeDoubleValue);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.BLACK);
			if (model != null) {
				if (model.getIdPlayer() == 0) {
					if (model.getTerrain() == Terrain.END_LINE) {
						g2.setBackground(Color.PINK);
						setBackground(Color.BLUE);
					} else {
						g2.setBackground(model.getTerrain().getColor());
						setBackground(model.getTerrain().getColor());
					}
				} else {
					g2.setBackground(PlayerManager.getInstance().getColorById(model.getIdPlayer()));
					setBackground(PlayerManager.getInstance().getColorById(model.getIdPlayer()));
				}
			} else {
				g2.setBackground(Color.WHITE);
				setBackground(Color.WHITE);
			}
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
