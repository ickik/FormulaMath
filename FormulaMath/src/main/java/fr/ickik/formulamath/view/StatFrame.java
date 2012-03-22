package fr.ickik.formulamath.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.Stats;
import fr.ickik.formulamath.model.player.PlayerManager;

public class StatFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 176710123412877650L;

	public StatFrame() {
		super(MainFrame.getTitle());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private JPanel displayStats() {
		JPanel statsPanel = new JPanel();
		PlayerManager playerManager = PlayerManager.getInstance();
		List<Player> finishList = new ArrayList<Player>();//playerManager.
		for (int i = 0; i < finishList.size(); i++) {
			Stats stats = new Stats(finishList.get(i));
			statsPanel.add(getPlayerStatsPanel(i, stats));
		}
		return statsPanel;
	}
	
	private JPanel getPlayerStatsPanel(int position, Stats stats) {
		JPanel panel = new JPanel();
		return panel;
	}
}
