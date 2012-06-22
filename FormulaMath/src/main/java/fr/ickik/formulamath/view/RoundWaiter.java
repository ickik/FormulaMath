package fr.ickik.formulamath.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Class that display a round composed by rounds. One round is colored
 * to represent the time which is running.
 * @author Ickik
 * @version 0.1.001, 19 June 2012
 * @since 0.3.4
 */
public final class RoundWaiter extends JComponent {

	private static final long serialVersionUID = 1L;
	private final int numberOfRound = 8;
    private int currentCompositeStart = 0;
    private final AlphaComposite[] composites;
    private Timer timer;
    private TimerTask task;
    private JFrame frame;
    private static final RoundWaiter singleton = new RoundWaiter();
    
    /**
     * Default constructor that needs a integer which is the number of
     * rounds to display.
     * @param n the number of rounds to display.
     */
	private RoundWaiter() {
        
        composites = new AlphaComposite[numberOfRound];
        for (int i = 0; i < numberOfRound - 2; i++) {
            composites[i] = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1 - ((float) i / (float) numberOfRound));
        }
        composites[numberOfRound - 2] = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0);
        composites[numberOfRound - 1] = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0);
        currentCompositeStart = (currentCompositeStart + 1) % numberOfRound;
        paintImmediately(0, 0, getWidth(), getHeight());
        task = new TimerTask() {
			
			@Override
			public void run() {
				currentCompositeStart = (currentCompositeStart + 1) % numberOfRound;
                paintImmediately(0, 0, getWidth(), getHeight());
                repaint();
			}
		};
        /*timer = new Timer(sec/(div), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentCompositeStart = (currentCompositeStart + 1) % numberOfRound;
                paintImmediately(0, 0, getWidth(), getHeight());
                repaint();
            }
        });*/
       // timer.start();
	}
	
	public static RoundWaiter getSingleton() {
		return singleton;
	}
	
	public void start() {
		timer = new Timer();
		
		timer.schedule(task, 0, 100);
		frame = new JFrame();
		setOpaque(false);
		
		frame.add(this);
		frame.setUndecorated(true);
		frame.setOpacity(0);
		frame.setSize(500, 500);
		/*GraphicsConfiguration gconf = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration();

		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(gconf);
		int w = frame.getWidth();
		int h = frame.getHeight();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth() - insets.left - insets.right - 5;
		double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - insets.top - insets.bottom - 5;
		frame.setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));*/
		frame.setVisible(true);
		frame.toFront();
	}
	
	/**
	 * Stop the timer that highlight the round.
	 */
	public void stop() {
		frame.dispose();
		timer.purge();
		task.cancel();
		timer=null;
		//timer.stop();
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int xCenter = width / 2;
        int yCenter = height / 2;
        int radius = (int)((width<height)?width/3:height/3);
        int size = radius / 4;
        g2.setColor(Color.RED);
        AffineTransform old = g2.getTransform();
        for (int i = 0; i < numberOfRound; i++) {
            g2.setComposite(composites[(currentCompositeStart + i) % numberOfRound]);
            g2.translate(xCenter,yCenter);
            g2.rotate(2 * Math.PI * i / numberOfRound);
            g2.fillOval(radius-size/3,-size/3, size, size);
            g2.setTransform(old);
        }
    }
}
