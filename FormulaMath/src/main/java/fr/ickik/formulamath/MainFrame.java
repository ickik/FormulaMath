package fr.ickik.formulamath;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.UpdateCaseListener;
import fr.ickik.formulamath.model.MapManager;
import fr.ickik.formulamath.model.PlayerManager;

public class MainFrame {

	private final JFrame mainFrame;
	public static final String NAME = "Formula Math";
	public static final String VERSION = "1.0.0";
	private int gridSize = 11;
	private final MapManager mapManager;
	private final PlayerManager playerManager;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private final List<List<JLabel>> caseList = new ArrayList<List<JLabel>>();
	private JPanel trayPanel;
	private static final int CASE_SIZE = 30;
	private Position leftCorner;

	public MainFrame(PlayerManager playerManager, MapManager mapManager) {
		mainFrame = new JFrame(NAME + " " + VERSION);
		this.playerManager = playerManager;
		this.mapManager = mapManager;
		createMainFrame();
		try {
			// mainFrame.setEnabled(false);
			this.playerManager.initStartPosition();
		} catch (FormulaMathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// playerManager.AIPlay();
	}

	private void createMainFrame() {
		mainFrame.add(getSplitPane(), BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	private JSplitPane getSplitPane() {
		trayPanel = getTrayPanel();
		try {
			playerManager.addUpdateCaseListener(new UpdateCaseListener() {

				public void updatePlayerCase(int x, int y, Player p) {
					int xTrayPanel = x - leftCorner.getX();
					int yTrayPanel = y - leftCorner.getY();
					log.debug("Position to update : ( {} , {} )", x, y);
					log.debug("Position on tray panel : ( {} , {} )",
							xTrayPanel, yTrayPanel);
					if (xTrayPanel > 0 && xTrayPanel < gridSize
							&& yTrayPanel > 0 && yTrayPanel < gridSize) {
						caseList.get(yTrayPanel).get(xTrayPanel)
								.setBackground(p.getPlayerColor());
						caseList.get(yTrayPanel).get(xTrayPanel).repaint();
						trayPanel.repaint();
						trayPanel.validate();
					}

				}
			});
		} catch (FormulaMathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				trayPanel, getMenuPanel());
		return split;
	}

	private JPanel getTrayPanel() {
		GridLayout gridLayout = new GridLayout(gridSize, gridSize);
		JPanel tray = new JPanel(gridLayout);
		Position center = mapManager.getStartPosition().get(
				mapManager.getStartPosition().size() / 2);

		int distance = (gridSize - 1) / 2;
		int xDepart = center.getX() - distance;
		int yDepart = center.getY() - distance;
		log.debug("Start coordinates : [{}, {}]", xDepart, yDepart);
		leftCorner = new Position(xDepart, yDepart);
		for (int i = 0; i < gridSize; i++) {
			List<JLabel> labelList = new ArrayList<JLabel>();
			for (int j = 0; j < gridSize; j++) {
				JLabel label = new JLabel();
				label.setOpaque(true);
				label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				label.setPreferredSize(new Dimension(CASE_SIZE, CASE_SIZE));
				if (xDepart + j >= 0 && yDepart + i >= 0
						&& xDepart + j < mapManager.getMapSize()
						&& yDepart + i < mapManager.getMapSize()) {
					Case c = mapManager.getCase(yDepart + i, xDepart + j);

					switch (c.getTerrain()) {
					case HERBE:
						// log.debug("[{}, {}] HERBE", xDepart + j, yDepart +
						// i);
						label.setBackground(Terrain.HERBE.getColor());
						break;
					case ROUTE:
						// log.debug("[{}, {}] ROUTE", xDepart + j, yDepart +
						// i);
						label.setBackground(Terrain.ROUTE.getColor());
						break;
					case START_LINE:
						// log.debug("[{}, {}] START lINE", xDepart + j, yDepart
						// + i);
						label.setBackground(Terrain.START_LINE.getColor());
						break;
					case END_LINE:
						// log.debug("[{}, {}] END LINE", xDepart + j, yDepart +
						// i);
						label.setBackground(Terrain.END_LINE.getColor());
						break;
					}

				} else {
					label.setBackground(Color.WHITE);
				}
				// log.debug("Color of the case({}, {}) : {}", new
				// Object[]{xDepart + i, yDepart + j,label.getBackground()});
				tray.add(label);
				labelList.add(label);
			}
			caseList.add(labelList);
		}
		return tray;
	}

	private JPanel getMenuPanel() {
		JPanel panel = new JPanel(new GridLayout(7, 1));
		final JCheckBox[] solution = new JCheckBox[5];
		Player player = playerManager.getPlayer(0);
		final List<Vector> vectorList = playerManager
				.getVectorsPossibilities(player);
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < 5; i++) {
			Vector v = vectorList.get(i);
			JCheckBox box = new JCheckBox("( " + v.getXMoving() + ", "
					+ v.getYMoving() + " )");
			group.add(box);
			solution[i] = box;
			panel.add(box);
		}

		JButton play = new JButton("Play");
		play.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int selectedPossibility = getSelectedCheckBox(solution);
				if (selectedPossibility == -1) {
					displayErrorMessage("No possibility selected");
					return;
				}
				playerManager.play(vectorList.get(selectedPossibility));
			}
		});
		panel.add(play);
		panel.add(getZoomPanel());
		return panel;
	}

	private JPanel getZoomPanel() {
		JPanel zoomPanel = new JPanel(new GridLayout(1, 2));
		JButton zoom = new JButton("+");
		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		JButton dezoom = new JButton("-");
		dezoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		zoomPanel.add(zoom);
		zoomPanel.add(dezoom);
		return zoomPanel;
	}

	private int getSelectedCheckBox(JCheckBox[] checkboxArray) {
		for (int i = 0; i < 5; i++) {
			if (checkboxArray[i].isSelected()) {
				return i;
			}
		}
		return -1;
	}

	private void displayErrorMessage(String msg) {

	}
}
