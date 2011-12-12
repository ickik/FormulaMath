package fr.ickik.formulamath;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	private int caseSize = 15;
	private final MapManager mapManager;
	private final PlayerManager playerManager;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private JScrollPane scrollPane;
	private List<List<JCase>> caseArrayList;
	private static final int MIN_ZOOM_SIZE = 10;
	private static final int MAX_ZOOM_SIZE = 50;
	
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
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.add(getSplitPane(), BorderLayout.CENTER);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}

	private JSplitPane getSplitPane() {
		JPanel trayPanel = getTrayPanel();
		playerManager.addUpdateCaseListener(new UpdateCaseListener() {

			public void updatePlayerCase(Player player) {
				mainFrame.repaint();
			}

			@Override
			public void updatePlayerPossibilities(Player player) {}

			@Override
			public void updateEndGamePanel(Player player) {
				// TODO Auto-generated method stub
				
			}
		});
		scrollPane = new JScrollPane(trayPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, getMenuPanel());
		split.setDividerLocation(new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.9).intValue());
		return split;
	}

	private JPanel getTrayPanel() {
		GridLayout gridLayout = new GridLayout(caseArrayList.size(), caseArrayList.size());
		JPanel tray = new JPanel(gridLayout);
		tray.setOpaque(true);
		for (List<JCase> list : caseArrayList) {
			for (JCase c : list) {
				tray.add(c);
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
				int distance = (caseArrayList.size() - mapManager.getMapSize()) / 2;
				int xTrayPanel = player.getPosition().getX() + distance;
				int yTrayPanel = player.getPosition().getY() + distance;
				for (int i = 0; i < vectorList.size();) {
					Vector v = vectorList.get(i);
					JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
					JCase c2 = caseArrayList.get(yTrayPanel - v.getYMoving()).get(xTrayPanel + v.getXMoving());
					
					Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
					if (isGrassIntersection(line)) {
						vectorList.remove(i);
					} else {
						i++;
					}
				}
				if (vectorList.isEmpty()) {
					displayErrorMessage("No possibility to play!!!");
					//System.exit(0);
				}
				for (int i = 0; i < vectorList.size(); i++) {
					Vector v = vectorList.get(i);
					if (v != null) {
						solution[i].setText("( " + v.getXMoving() + ", " + v.getYMoving() + " )");
					} else {
						solution[i].setText("");
					}
					solution[i].setEnabled(v != null);
					solution[i].setSelected(false);
				}
				for (int i = vectorList.size(); i < 5; i++) {
					solution[i].setText("");
					solution[i].setEnabled(false);
					solution[i].setSelected(false);
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
				Player player = playerManager.getCurrentPlayer();
				int distance = (caseArrayList.size() - mapManager.getMapSize()) / 2;
				JCase c = caseArrayList.get(player.getPosition().getY() + distance).get(player.getPosition().getX() + distance);
				JCase c2 = caseArrayList.get(player.getPosition().getY() - vector.getYMoving() + distance).get(player.getPosition().getX() + vector.getXMoving() + distance);
				
				Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
				
				if (isEndLineIntersection(line)) {
					displayErrorMessage("end");
				}
				
				if (playerManager.play(vector)) {
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
				if (caseSize > MIN_ZOOM_SIZE) {
					caseSize++;
					repaintTrayPanel();
				}
			}
		});
		JButton dezoom = new JButton("-");
		dezoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (caseSize < MAX_ZOOM_SIZE) {
					caseSize--;
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
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getVerticalScrollBar().getModel().setValue(scrollPane.getVerticalScrollBar().getModel().getValue() + 40);
			}
		});
		JButton down = new JButton("d");
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getVerticalScrollBar().getModel().setValue(scrollPane.getVerticalScrollBar().getModel().getValue() - 40);
			}
		});
		JButton left = new JButton("l");
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getHorizontalScrollBar().getModel().setValue(scrollPane.getHorizontalScrollBar().getModel().getValue() - 40);
			}
		});
		JButton right = new JButton("r");
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getHorizontalScrollBar().getModel().setValue(scrollPane.getHorizontalScrollBar().getModel().getValue() + 40);
			}
		});
		
//		JButton centered = new JButton("center");
//		centered.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				playerManager.getCurrentPlayer();
//			}
//		});
		panel.add(up, BorderLayout.NORTH);
		panel.add(left, BorderLayout.WEST);
		panel.add(right, BorderLayout.EAST);
		panel.add(down, BorderLayout.SOUTH);
//		panel.add(centered, BorderLayout.CENTER);
		return panel;
	}
	
	private ActionListener getPlayerFocusListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollPane.getHorizontalScrollBar().setValue(0);
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		};
	}
	
	private void repaintTrayPanel() {
		Dimension d = new Dimension(caseSize, caseSize);
		for (List<JCase> list : caseArrayList) {
			for (JCase c : list) {
				c.setSize(d);
			}
		}
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
	
	private boolean isGrassIntersection(Shape shape) {
		return checkIntersection(shape, Terrain.HERBE);
	}
	
	private boolean isEndLineIntersection(Shape shape) {
		return checkIntersection(shape, Terrain.END_LINE);
	}
	
	private boolean checkIntersection(Shape shape, Terrain terrain) {
		for (List<JCase> caseList : caseArrayList) {
			for (JCase c : caseList) {
				if (c.getModel() != null && c.getModel().getTerrain() == terrain) {
					if (shape.intersects(c.getX(), c.getY(), c.getWidth(), c.getHeight())) {
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
