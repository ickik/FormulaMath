package fr.ickik.formulamath.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;

/**
 * Graphic component which represents a case in the map. Every side of the case
 * has the same size. A case is a shape (Rectangle) draw on the screen. The model
 * of the case defines the color of the case background.
 * @author Ickik.
 * @version 0.1.005, 9 July 2012.
 */
public class JCase extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8669794576446631541L;

	private Rectangle2D shape;
	private final CaseModel model;
	private static final Color[] colorList = new Color[] { Color.RED, Color.BLACK, Color.BLUE, Color.YELLOW };
	
	/**
	 * Constructor of the component. The size in pixel must be parameterized and
	 * the model of the component must be completed.
	 * @param size the size in pixel.
	 * @param model the model of the component.
	 */
	public JCase(int size, CaseModel model) {
		this.model = model;
		setOpaque(true);
		Dimension dimension = new Dimension(size, size);
		setSize(dimension);
		shape = new Rectangle2D.Double(0.0, 0.0, size, size);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			if (model != null && model.getIdPlayer() == 0 && model.getField() == Field.FINISHING_LINE) {
				drawEndLine(g2);
				if (model != null && model.isPaintBorder()) {
					g2.setColor(Color.BLACK);
					g2.draw(shape);
				}
				return ;
			}
			Color color = updateBackGroundColor();
			g2.setColor(color);
			g2.fill(shape);
			paintVibrator(g2);
			if (model != null && model.isPaintBorder()) {
				g2.setColor(Color.BLACK);
				g2.draw(shape);
			}
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
	
	private Color updateBackGroundColor() {
		if (model != null) {
			if (model.getBackgroundColor() == null) {
				if (model.getIdPlayer() == MapManager.EMPTY_PLAYER) {
					return model.getField().getColor();
				}
				return colorList[model.getIdPlayer() - 1];
			}
			return model.getBackgroundColor();
		}
		return Color.WHITE;
	}
	
	private void paintVibrator(Graphics2D g2) {
		if (model == null || model.getBorderCaseSide() == null) {
			return ;
		}
		double size = shape.getBounds2D().getWidth() / 10;
		double size2 = shape.getBounds2D().getWidth() / 8;
		double x = 0;
		double y = 0;
		double width = 0;
		double height = 0;
		double xShifting = 0;
		double yShifting = 0;
		switch(model.getBorderCaseSide()) {
		case TOP:
			width = size2;
			height = size;y++;
			xShifting = width;
			break;
		case LEFT:
			width = size;x++;
			height = size2;
			yShifting = height;
			break;
		case RIGHT :
			x = shape.getBounds2D().getWidth() - size;
			width = size;
			height = size2;
			yShifting = height;
			break;
		case BOTTOM:
			y = shape.getBounds2D().getHeight() - size;
			width = size2;
			height = size;
			xShifting = width;
			break;
		default:
			return;
		}
		for (int j = 0; j < 8; j++) {
			Shape s = new Rectangle2D.Double(x + (j * xShifting), y + (j * yShifting), width, height);
			if (j % 2 == 0) {
				g2.setColor(Color.RED);
			} else {
				g2.setColor(Color.WHITE);
			}
			g2.fill(s);
		}
	}
	
	@Override
	public void paintComponents(Graphics g) {
		paint(g);
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
	}
	
	@Override
	public void setMaximumSize(Dimension maximumSize) {}
	
	@Override
	public void setMinimumSize(Dimension minimumSize) {}

	@Override
	public Dimension getMaximumSize() {
		return getSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getSize();
	}
}
