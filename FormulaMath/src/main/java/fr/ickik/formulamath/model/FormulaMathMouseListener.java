package fr.ickik.formulamath.model;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.MouseInputListener;

import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.view.JCase;

/**
 * Implementation of the MouseListener.
 * @author Ickik
 * @version 0.1.004, 30 August 2012
 * @since 0.3.6
 */
public class FormulaMathMouseListener implements MouseInputListener {

	private Position startPosition;
	private JScrollPane scrollPanel;
	private JLabel descriptionLabel;
	private List<List<JCase>> caseArrayList;
	
	public FormulaMathMouseListener() {
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			Position p = getJCasePosition(arg0.getPoint());
			if (startPosition == null) {
				startPosition = p;
				JCase c = caseArrayList.get(p.getY()).get(p.getX());
				c.getGraphics().drawLine(0, 0, c.getWidth(), c.getHeight());
				c.getGraphics().drawLine(0, c.getHeight(), c.getWidth(), 0);
			} else {
				JCase c = caseArrayList.get(p.getY()).get(p.getX());
				c.getGraphics().drawLine(0, 0, c.getWidth(), c.getHeight());
				c.getGraphics().drawLine(0, c.getHeight(), c.getWidth(), 0);
				descriptionLabel.setText("Vector (" + Integer.toString(getHorizontalCaseNumber(startPosition, p)) + ", " + Integer.toString(getVerticalCaseNumber(startPosition, p)) + ") - Distance : " + Double.toString(getDistance(startPosition, p)) + " case");
				startPosition = null;
				scrollPanel.repaint();
			}
		}
	}
	
	private Position getJCasePosition(Point point) {
		int w = scrollPanel.getHorizontalScrollBar().getModel().getValue();
		int h = scrollPanel.getVerticalScrollBar().getModel().getValue();
		for (int i = 0; i < caseArrayList.size(); i++) {
			for (int j = 0; j < caseArrayList.size(); j++) {
				JCase c = caseArrayList.get(i).get(j);
				/*if (c.contains(point)) {
					return new Position(j, i);
				}*/
				if (c.getLocation().getX() <= point.getX() + w && c.getLocation().getX() + c.getWidth() >= point.getX() + w
						&& c.getLocation().getY() <= point.getY() + h && c.getLocation().getY() + c.getHeight() >= point.getY() + h) {
					return new Position(j, i);
				}
			}
		}
		return null;
	}
	
	private int getHorizontalCaseNumber(Position start, Position end) {
		return end.getX() - start.getX();
	}
	
	private int getVerticalCaseNumber(Position start, Position end) {
		return start.getY() - end.getY();
	}
	
	private double getDistance(Position start, Position end) {
		int powX = start.getX() - end.getX();
		int powY = start.getY() - end.getY();
		double dist = Math.sqrt(powX * powX + powY * powY);
		dist *= 100.0;
		dist = Math.floor(dist+0.5);
		dist /= 100.0;
		return dist;
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

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
	//	startPoint = null;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		/*if (arg0.getButton() == MouseEvent.BUTTON1) {
			if (startPoint == null) {
				startPoint = arg0.getPoint();
			} else {
				int horizontalValue= scrollPanel.getHorizontalScrollBar().getModel().getValue();
				int verticalValue= scrollPanel.getVerticalScrollBar().getModel().getValue();
				int hDiff = (int) (arg0.getX() - startPoint.getX());
				int vDiff = (int) (arg0.getY() - startPoint.getY());
				scrollPanel.getHorizontalScrollBar().getModel().setValue(horizontalValue + hDiff);
				scrollPanel.getVerticalScrollBar().getModel().setValue(verticalValue + vDiff);
				startPoint = arg0.getPoint();
			}
		}*/
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

	public void setCaseArrayList(List<List<JCase>> caseArrayList) {
		this.caseArrayList = caseArrayList;
	}
}
