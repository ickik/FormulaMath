package fr.ickik.formulamath.model;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;

public class FormulaMathMouseListenerClass implements MouseListener {

	private Point startPoint;
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

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
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		startPoint = null;
	}

}
