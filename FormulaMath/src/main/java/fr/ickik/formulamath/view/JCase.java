package fr.ickik.formulamath.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import fr.ickik.formulamath.Field;
import fr.ickik.formulamath.model.CaseModel;
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

	private Shape shape;
	private CaseModel model;
	
	/**
	 * Constructor of the component. The size in pixel must be parameterized.
	 * @param size the size in pixel.
	 */
	public JCase(int size) {
		setOpaque(true);
		Dimension dimension = new Dimension(size, size);
		setSize(dimension);
		double sizeDoubleValue = (new Integer(size+1)).doubleValue();
		shape = new Rectangle2D.Double(0.0, 0.0, sizeDoubleValue, sizeDoubleValue);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			if (model != null && model.getIdPlayer() == 0) {
				if (model.getField() == Field.END_LINE) {
					drawEndLine(g2);
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
	
	private void drawEndLine(Graphics2D g2) {
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
	}
	
	private void updateBackGroundColor() {
		if (model != null) {
			if (model.getIdPlayer() == 0) {
				setBackground(model.getField().getColor());
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

	/**
	 * Set the model of the component.
	 * @param model the model of the component.
	 */
	public void setModel(CaseModel model) {
		this.model = model;
	}

	/**
	 * Return the model of the component.
	 * @return the model of the component.
	 */
	public CaseModel getModel() {
		return model;
	}
	
	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		shape = new Rectangle2D.Double(0.0, 0.0, d.getWidth(), d.getHeight());
		repaint();
	}
}
