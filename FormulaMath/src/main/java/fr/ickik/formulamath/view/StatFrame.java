package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.Stats;

/**
 * JFrame which displays all statistics about players. This frame disable the
 * calling frame the time the user close this frame. It display teh average distance
 * per turn, the variance, the square type and a graphic which resume the number of
 * vector played during the game.
 * @author Ickik
 * @version 0.1.005, 15 mai 2012
 */
public final class StatFrame extends AbstractFormulaMathFrame {

	private final FormulaMathController controller;
	private final JPanel statPanel;

	/**
	 * Constructor which disabled the calling frame given in argument. The
	 * calling frame is disable the time this frame is existing. It enable it
	 * when the user close this frame.
	 * @param callingFrame the calling frame to disable.
	 */
	public StatFrame(FormulaMathController controller) {
		this.controller = controller;
		getFrame().addWindowListener(createWindowListener());
		JPanel panel = new JPanel(new BorderLayout());
		statPanel = new JPanel();
		panel.add(statPanel, BorderLayout.CENTER);
		panel.add(createButton(), BorderLayout.SOUTH);
		getFrame().add(panel);
	}
	
	public void display(int playerNumber, List<Player> finishPlayerList) {
		statPanel.removeAll();
		statPanel.add(displayStats(playerNumber, finishPlayerList));
		displayFrame();
		getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
				controller.closeStatFrame();
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		};
	}
	
	private JScrollPane displayStats(int playerNumber, List<Player> finishPlayerList) {
		JPanel statsPanel = new JPanel(new GridLayout(playerNumber, 1));
//		for (int i = 0; i < playerNumber; i++) {
//			Stats stats = new Stats(finishPlayerList.get(i));
//			statsPanel.add(getPlayerStatsPanel(i, stats));
//		}
		int position = 1;
		for (Player player : finishPlayerList) {
			if (player != null) {
				Stats stats = new Stats(player);
				statsPanel.add(getPlayerStatsPanel(position, stats));
				position++;
			}
		}
		return new JScrollPane(statsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
	
	private JPanel getPlayerStatsPanel(int position, Stats stats) {
		JPanel panel = new JPanel(new GridLayout(1, 2));
		JPanel panelLbl = new JPanel(new GridLayout(3, 2));
		String playerId = Integer.toString(position);
		panelLbl.add(new JLabel(playerId +"Average"));
		panelLbl.add(new JLabel(Double.toString(stats.getAverageDistance())));
		panelLbl.add(new JLabel(playerId+"Variance"));
		panelLbl.add(new JLabel(Double.toString(stats.getVariance())));
		panelLbl.add(new JLabel(playerId+"Square type"));
		panelLbl.add(new JLabel(Double.toString(stats.getSquareType())));
		panel.add(panelLbl);
		//panel.add(createGraph(stats.getVectorCountMap()));
		return panel;
	}
	
	private JPanel createButton() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
		JButton replay = new JButton("Replay");
		replay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.modelReinitialization();
			}
		});
		JButton button = new JButton("Close");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.closeStatFrame();
			}
		});
		buttonPanel.add(button);
		return buttonPanel;
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
