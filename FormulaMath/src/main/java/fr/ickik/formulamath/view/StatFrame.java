package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.Stats;
import fr.ickik.formulamath.model.player.PlayerManager;

/**
 * JFrame which displays all statistics about players. This frame disable the
 * calling frame the time the user close this frame. It display teh average distance
 * per turn, the variance, the square type and a graphic which resume the number of
 * vector played during the game.
 * @author Ickik
 * @version 0.1.002, 20 apr. 2012
 */
public final class StatFrame extends AbstractFormulaMathFrame {

	private final JFrame callingFrame;
	private final JFrame statFrame;

	/**
	 * Constructor which disabled the calling frame given in argument. The
	 * calling frame is disable the time this frame is existing. It enable it
	 * when the user close this frame.
	 * @param callingFrame the calling frame to disable.
	 */
	public StatFrame(JFrame callingFrame) {
		this.callingFrame = callingFrame;
		statFrame = new JFrame(getTitle());
		statFrame.addWindowListener(createWindowListener());
		callingFrame.setEnabled(false);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(displayStats(), BorderLayout.CENTER);
		panel.add(createButton(), BorderLayout.SOUTH);
		statFrame.add(panel);
		statFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		statFrame.setIconImages(getIconList());
		centeredFrame(statFrame);
		statFrame.setVisible(true);
	}
	
	private WindowListener createWindowListener() {
		return new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowClosing(WindowEvent arg0) {}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				callingFrame.setEnabled(true);
				statFrame.dispose();
				callingFrame.toFront();
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		};
	}
	
	private JScrollPane displayStats() {
		PlayerManager playerManager = PlayerManager.getInstance();
		JPanel statsPanel = new JPanel(new GridLayout(playerManager.getPlayerList().size(), 1));
		List<Player> finishList = new ArrayList<Player>();//playerManager.
		for (int i = 0; i < finishList.size(); i++) {
			Stats stats = new Stats(finishList.get(i));
			statsPanel.add(getPlayerStatsPanel(i, stats));
		}
		return new JScrollPane(statsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
	private JPanel getPlayerStatsPanel(int position, Stats stats) {
		JPanel panel = new JPanel(new GridLayout(1, 2));
		JPanel panelLbl = new JPanel(new GridLayout(3, 2));
		panelLbl.add(new JLabel("Average"));
		panelLbl.add(new JLabel(Double.toString(stats.getAverageDistance())));
		panelLbl.add(new JLabel("Variance"));
		panelLbl.add(new JLabel(Double.toString(stats.getVariance())));
		panelLbl.add(new JLabel("Square type"));
		panelLbl.add(new JLabel(Double.toString(stats.getSquareType())));
		panel.add(panelLbl);
		//panel.add(createGraph(stats.getVectorCountMap()));
		return panel;
	}
	
	private JButton createButton() {
		JButton button = new JButton();
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				callingFrame.setEnabled(true);
				statFrame.dispose();
				callingFrame.toFront();
			}
		});
		return button;
	}
	
	private JPanel createGraph(Map<Vector, Integer> vectorCountMap) {
//	DefaultCategoryDataset cat = new DefaultCategoryDataset();
//	cat.addValue(1.0, "(1,0)", "(1,0)");
//	cat.addValue(4.0, "(1,1)", "(1,1)");
//	JFreeChart chart = ChartFactory.createBarChart3D("", "Vector", "Number of move", cat, PlotOrientation.VERTICAL, false, false, false);
//	ChartPanel panel = new ChartPanel(chart);
//	JFrame f = new JFrame();
//	f.add(panel);
//	f.setVisible(true);
		return new JPanel();
	}
}
