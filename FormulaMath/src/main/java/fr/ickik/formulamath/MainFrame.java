package fr.ickik.formulamath;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.UpdateCaseListener;
import fr.ickik.formulamath.model.MapManager;
import fr.ickik.formulamath.model.PlayerManager;

/**
 * This class create the main frame of the application.
 * @author Ickik.
 * @version 0.1.000, 30 sept. 2011.
 */
public class MainFrame {

	private final JFrame mainFrame;
	public static final String NAME = "Formula Math";
	public static final String VERSION = "1.0.0";
	private int gridSize = 25;
	private int caseSize = 15;
	private final MapManager mapManager;
	private final PlayerManager playerManager;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private List<List<JCase>> caseArrayList;
	private JPanel trayPanel;
	private static final int MIN_ZOOM_SIZE = 3;
	private static final int MAX_ZOOM_SIZE = 50;
	private Position leftCorner;

	public MainFrame(PlayerManager playerManager, MapManager mapManager) {
		mainFrame = new JFrame(NAME + " " + VERSION);
		this.playerManager = playerManager;
		this.mapManager = mapManager;
		caseArrayList = new ArrayList<List<JCase>>(mapManager.getMapSize() + 20);
		initMap();
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
	
	private void initMap() {
		int sideSize = mapManager.getMapSize() + 20;
		int x = 0;
		int y = 0;
		for (int i = 0; i < sideSize; i++) {
			x = 0;
			List<JCase> caseList = new ArrayList<JCase>(sideSize);
			for (int j = 0; j < sideSize; j++) {
				JCase cas = new JCase(caseSize);
				if (i >= 10 && i <= (sideSize - 10) && j >= 10 && j <= (sideSize - 10)) {
					cas.setModel(mapManager.getCase(y, x));
					x++;
				}
				caseList.add(cas);
			}
			caseArrayList.add(caseList);
			if (i >= 10 && i <= (sideSize - 10)) {
				y++;
			}
		}
	}

	private void createMainFrame() {
		mainFrame.add(getSplitPane(), BorderLayout.CENTER);
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	private JSplitPane getSplitPane() {
		trayPanel = new JPanel();
		trayPanel.add(getTrayPanel());
		playerManager.addUpdateCaseListener(new UpdateCaseListener() {

			public void updatePlayerCase(Player player) {
				int xTrayPanel = player.getPosition().getX() - leftCorner.getX();
				int yTrayPanel = player.getPosition().getY() - leftCorner.getY();
				if (xTrayPanel > 0 && xTrayPanel < gridSize && yTrayPanel > 0 && yTrayPanel < gridSize) {
					updateTrayPanel();
				}
			}

			@Override
			public void updatePlayerPossibilities(Player player) {}

			@Override
			public void updateEndGamePanel(Player player) {
				// TODO Auto-generated method stub
				
			}
		});
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, trayPanel, getMenuPanel());
		split.setDividerLocation(0.8);
		return split;
	}

	private JPanel getTrayPanel() {
		GridLayout gridLayout = new GridLayout(gridSize, gridSize);
		JPanel tray = new JPanel(gridLayout);
		tray.setOpaque(true);
		int distance = (gridSize - 1) / 2;
		int xDepart, yDepart;
		if (leftCorner == null) {
			Position center = mapManager.getStartPosition().get(mapManager.getStartPosition().size() / 2);
			xDepart = center.getX() - distance;
			yDepart = center.getY() - distance;
			log.debug("Start coordinates : [{}, {}]", xDepart, yDepart);
			leftCorner = new Position(xDepart, yDepart);
		} else {
			xDepart = leftCorner.getX();
			yDepart = leftCorner.getY();
		}
		if (xDepart < 0) {
			xDepart = 0;
		}
		if (yDepart < 0) {
			yDepart = 0;
		}
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				tray.add(caseArrayList.get(yDepart + i).get(xDepart + j));
			}
		}
		return tray;
	}

	private JPanel getMenuPanel() {
		final JPanel panel = new JPanel(new GridLayout(4, 1));
		JButton play = new JButton("Play");
		panel.add(getChoicePanel(play));
		panel.add(play);
		panel.add(getDirectionalPanel());
		panel.add(getZoomPanel());
		return panel;
	}
	
	private JPanel getChoicePanel(final JButton play) {
		final JPanel panel = new JPanel(new GridLayout(5, 1));
		final JCheckBox[] solution = new JCheckBox[5];
		final List<Vector> vectorList = new ArrayList<Vector>(5);
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < 5; i++) {
			JCheckBox box = new JCheckBox("");
			box.setEnabled(false);
			box.setSelected(false);
			group.add(box);
			solution[i] = box;
			panel.add(box);
		}
		
		playerManager.addUpdateCaseListener(new UpdateCaseListener() {
			
			@Override
			public void updatePlayerPossibilities(Player player) {
				vectorList.clear();
				vectorList.addAll(playerManager.getVectorsPossibilities(player));
				for (int i = 0; i < 5; i++) {
					//int xTrayPanel = player.getPosition().getX() - leftCorner.getX();
					//int yTrayPanel = player.getPosition().getY() - leftCorner.getY();
//					Vector v = vectorList.get(i);
//					new Line2D.Double(player.getPosition().getX(), player.getPosition().getX(), 0,0);
				}
				for (int i = 0; i < 5; i++) {
//					Vector v = vectorList.get(i);
//					if (v != null) {
//						solution[i].setText("( " + v.getXMoving() + ", " + v.getYMoving() + " )");
//					} else {
//						solution[i].setText("");
//					}
//					solution[i].setEnabled(v != null);
//					solution[i].setSelected(false);
				}
				panel.revalidate();
			}
			
			@Override
			public void updatePlayerCase(Player player) {}

			@Override
			public void updateEndGamePanel(Player player) {
				play.setText("End");
				removeButtonListener(play);
				play.addActionListener(getEndGameListener());
				panel.removeAll();
				panel.add(getFinishLabel());
				panel.validate();
			}
		});
		
		play.addActionListener(getPlayActionListener(vectorList, solution));
		return panel;
	}
	
	private void removeButtonListener(JButton button) {
		ActionListener[] listenerArray = button.getActionListeners();
		for (ActionListener l : listenerArray) {
			button.removeActionListener(l);
		}
	}
	
	private ActionListener getEndGameListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
	}
	
	private ActionListener getPlayActionListener(final List<Vector> vectorList, final JCheckBox[] solution) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedPossibility = getSelectedCheckBox(solution);
				if (selectedPossibility == -1) {
					displayErrorMessage("No possibility selected");
					return;
				}
				Vector vector = vectorList.get(selectedPossibility);
				if (playerManager.play(vector)) {
					leftCorner.setX(leftCorner.getX() + vectorList.get(selectedPossibility).getXMoving());
					leftCorner.setY(leftCorner.getY() - vectorList.get(selectedPossibility).getYMoving());
				}
			}
		};
	}
	
	private JLabel getFinishLabel() {
		JLabel label = new JLabel("");
		return label;
	}

	private JPanel getZoomPanel() {
		JPanel zoomPanel = new JPanel(new GridLayout(1, 2));
		JButton zoom = new JButton("+");
		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (gridSize > MIN_ZOOM_SIZE) {
					leftCorner.setX(leftCorner.getX() + 1);
					leftCorner.setY(leftCorner.getY() + 1);
					gridSize-=2;
					caseSize += 1;
					repaintTrayPanel();
				}
			}
		});
		JButton dezoom = new JButton("-");
		dezoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (gridSize < MAX_ZOOM_SIZE) {
					leftCorner.setX(leftCorner.getX() - 1);
					leftCorner.setY(leftCorner.getY() - 1);
					gridSize+=2;
					caseSize -= 1;
					repaintTrayPanel();
				}
			}
		});
		zoomPanel.add(zoom);
		zoomPanel.add(dezoom);
		return zoomPanel;
	}

	private JPanel getDirectionalPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JButton up = new JButton("up");
		up.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				leftCorner.setY(leftCorner.getY() - 1);
				updateTrayPanel();
			}
		});
		JButton down = new JButton("d");
		down.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				leftCorner.setY(leftCorner.getY() + 1);
				updateTrayPanel();
			}
		});
		JButton left = new JButton("l");
		left.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				leftCorner.setX(leftCorner.getX() - 1);
				updateTrayPanel();
			}
		});
		JButton right = new JButton("r");
		right.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				leftCorner.setX(leftCorner.getX() + 1);
				updateTrayPanel();
			}
		});
		panel.add(up, BorderLayout.NORTH);
		panel.add(left, BorderLayout.WEST);
		panel.add(right, BorderLayout.EAST);
		panel.add(down, BorderLayout.SOUTH);
		return panel;
	}
	
	private void updateTrayPanel() {
		int distance = (gridSize - 1) / 2;
		int xDepart, yDepart;
		if (leftCorner == null) {
			Position center = mapManager.getStartPosition().get(mapManager.getStartPosition().size() / 2);
			xDepart = center.getX() - distance;
			yDepart = center.getY() - distance;
			log.debug("Start coordinates : [{}, {}]", xDepart, yDepart);
			leftCorner = new Position(xDepart, yDepart);
		} else {
			xDepart = leftCorner.getX();
			yDepart = leftCorner.getY();
		}
		if (xDepart < 0) {
			xDepart = 0;
		}
		if (yDepart < 0) {
			yDepart = 0;
		}
		JPanel panel = (JPanel) trayPanel.getComponent(0);
		panel.removeAll();
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				panel.add(caseArrayList.get(yDepart + i).get(xDepart + j));
			}
		}
		mainFrame.validate();
		//mainFrame.repaint();
	}
	
	private void repaintTrayPanel() {
		trayPanel.removeAll();
		trayPanel.repaint();
		trayPanel.add(getTrayPanel());
		mainFrame.validate();
	}

	private int getSelectedCheckBox(JCheckBox[] checkboxArray) {
		for (int i = 0; i < 5; i++) {
			if (checkboxArray[i].isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean isIntersection(Shape shape) {
		for (List<JCase> caseList : caseArrayList) {
			for (JCase c : caseList) {
				if (c.getModel() != null && c.getModel().getTerrain() == Terrain.END_LINE) {
					if (shape.intersects(c.getLocation().getX(), c.getLocation().getY(), c.getLocation().getX() + c.getWidth(), c.getLocation().getX() + c.getHeight())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void displayErrorMessage(String msg) {
		JOptionPane.showMessageDialog(mainFrame, msg, NAME + " " + VERSION + " - ERROR!", JOptionPane.ERROR_MESSAGE);
	}
}
