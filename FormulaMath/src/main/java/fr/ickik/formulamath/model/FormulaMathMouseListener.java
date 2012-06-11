package fr.ickik.formulamath.model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

public class FormulaMathMouseListener implements MouseListener {

	private Point startPoint;
	private final Graphics2D graphics;
	
	public FormulaMathMouseListener(Graphics2D graphics) {
		this.graphics = graphics;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getModifiers() == InputEvent.BUTTON1_DOWN_MASK) {
			
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (startPoint == null) {
			startPoint = arg0.getPoint();
		} else {
			Line2D line = new Line2D.Double(startPoint, arg0.getPoint());
			graphics.drawLine((int) startPoint.getX(), (int) startPoint.getY(), arg0.getX(), arg0.getX());
			graphics.draw(line);
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		startPoint = null;
	}

}
