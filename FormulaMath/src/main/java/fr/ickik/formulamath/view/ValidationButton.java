package fr.ickik.formulamath.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;

/**
 * 
 * @author Ickik.
 * @version 0.1.000, th  2013.
 * @since 0.
 */
public class ValidationButton extends JButton {

	private static final long serialVersionUID = 1L;

	public ValidationButton(String text) {
		super(text);
		setForeground(Color.WHITE);
		setContentAreaFilled(false);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				ValidationButton.this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));	
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				ValidationButton.this.setBorder(BorderFactory.createEmptyBorder());
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				ValidationButton.this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				ValidationButton.this.setBorder(BorderFactory.createEmptyBorder());
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		GradientPaint gradientPaint = new GradientPaint(0, 0, (new Color(32, 192, 255)) , 0, getHeight(), new Color(16, 80, 255).darker());
		g2.setPaint(gradientPaint);
		g2.fillRect(0, 0, getWidth(), getHeight());
		//g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
		g2.dispose();
		super.paintComponent(g);
	}

}
