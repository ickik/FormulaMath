package fr.ickik.formulamath.model;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputListener;

/**
 * Implementation of the MouseListener.
 * @author Ickik
 * @version 0.1.001, 28 June 2012
 * @since 0.3.6
 */
public class FormulaMathMouseListener implements MouseListener, MouseInputListener {

	private Point startPoint;
	private JScrollPane scrollPanel;
	private JLabel descriptionLabel;
	private int caseSize;
	
	public FormulaMathMouseListener(int caseSize) {
		this.setCaseSize(caseSize);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (startPoint == null) {
			startPoint = arg0.getPoint();
			scrollPanel.getGraphics().drawLine(arg0.getX() - 10, arg0.getY() - 10, arg0.getX() + 10, arg0.getY() + 10);
			scrollPanel.getGraphics().drawLine(arg0.getX() - 10, arg0.getY() + 10, arg0.getX() + 10, arg0.getY() - 10);
		} else {
			scrollPanel.getGraphics().drawLine(arg0.getX() - 10, arg0.getY() - 10, arg0.getX() + 10, arg0.getY() + 10);
			scrollPanel.getGraphics().drawLine(arg0.getX() - 10, arg0.getY() + 10, arg0.getX() + 10, arg0.getY() - 10);
			descriptionLabel.setText("Vector (" + getHorizontalCaseNumber(startPoint, arg0.getPoint()) + ", " + getVerticalCaseNumber(startPoint, arg0.getPoint()) + ") - Distance : " + Double.toString(getDistance(startPoint, arg0.getPoint())) + " px");
			startPoint = null;scrollPanel.repaint();
		}
	}
	
	private double getHorizontalCaseNumber(Point start, Point end) {
		return Math.floor((Math.sqrt((start.getX() - end.getX()) * (start.getX() - end.getX())) / caseSize) + 0.5);
	}
	
	private double getVerticalCaseNumber(Point start, Point end) {
		return Math.floor((Math.sqrt((start.getY() - end.getY()) * (start.getY() - end.getY())) / caseSize) + 0.5);
	}
	
	private double getDistance(Point start, Point end) {
		double powX = start.getX() - end.getX();
		double powY = start.getY() - end.getY();
		double dist = Math.sqrt(powX * powX + powY * powY);
		dist *= 100.0;
		dist = Math.floor(dist+0.5);
		dist /= 100.0;
		return dist;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		/*if (startPoint == null) {
			startPoint = arg0.getPoint();
		}*//* else {
			Line2D line = new Line2D.Double(startPoint, arg0.getPoint());
			graphics.drawLine((int) startPoint.getX(), (int) startPoint.getY(), arg0.getX(), arg0.getX());
			graphics.draw(line);
		}*/
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		//startPoint = null;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub+
		/*if (startPoint != null) {
			Line2D line = new Line2D.Double(startPoint, arg0.getPoint());
			graphics.drawLine((int) startPoint.getX(), (int) startPoint.getY(), arg0.getX(), arg0.getY());
			graphics.draw(line);
		}*/
	}
	
	public void setDescriptionLabel(JLabel descriptionLabel) {
		this.descriptionLabel = descriptionLabel;
	}
	
	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPanel = scrollPane;
	}

	public void setCaseSize(int caseSize) {
		this.caseSize = caseSize;
	}
}
