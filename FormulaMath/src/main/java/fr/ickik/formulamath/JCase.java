package fr.ickik.formulamath;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import fr.ickik.formulamath.model.PlayerManager;

/**
 * Graphic component which represents a case in the map. Every side of the case
 * has the same size. A case is a shape (Rectangle) draw on the screen. The model
 * of the case defines the color of the case background.
 * @author Ickik.
 * @version 0.1.000, 3 dec 2011.
 */
@SuppressWarnings("serial")
public class JCase extends JComponent {

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
			if (model != null && model.getIdPlayer() == 0) {
				if (model.getTerrain() == Terrain.END_LINE) {
					double size = shape.getBounds2D().getWidth() / 4;
					int cpt = 0;
					for (int i = 0; i < 4; i++) {
						for (int j = 0; j < 4; j++) {
							Shape s = new Rectangle2D.Double(j * size, i * size, size, size);
							if (cpt % 2 == 0) {
								g2.setColor(Color.BLACK);
							} else {
								g2.setColor(Color.WHITE);
							}
							g2.fill(s);
							cpt++;
						}
						cpt--;
					}
					g2.setColor(Color.BLACK);
					g2.draw(shape);
					return ;
				}
			}
			updateBackGroundColor();
			g2.setColor(getBackground());
			g2.fill(shape);
			g2.setColor(Color.BLACK);
			g2.draw(shape);
		}
	}
	
	private void updateBackGroundColor() {
		if (model != null) {
			if (model.getIdPlayer() == 0) {
				setBackground(model.getTerrain().getColor());
			} else {
				setBackground(PlayerManager.getInstance().getColorById(model.getIdPlayer()));
			}
		} else {
			setBackground(Color.WHITE);
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
