package fr.ickik.formulamath;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.controler.UpdateCaseListener;
import fr.ickik.formulamath.model.MapManager;
import fr.ickik.formulamath.model.PlayerManager;
import fr.ickik.formulamath.model.PropertiesModel;
import fr.ickik.formulamath.view.AboutFrame;
import fr.ickik.formulamath.view.JCase;

/**
 * This class create the main frame of the application.
 * @author Ickik.
 * @version 0.1.003, 30 sept. 2011.
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
		log.debug(mapManager.toString());
		for (RoadDirectionInformation r : mapManager.getRoadDirectionInformationList()) {
			log.debug(r.toString());
		}
		mainFrame = new JFrame(NAME + " " + VERSION);
		ChuckNorrisTimer.getInstance(mainFrame);
		this.playerManager = playerManager;
		this.mapManager = mapManager;
		caseArrayList = new ArrayList<List<JCase>>(mapManager.getMapSize() + 20);
		initMap();
		createMainFrame();
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
		//mainFrame.setJMenuBar(getMenuBar());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {}
			
			@Override
			public void windowIconified(WindowEvent arg0) {}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				try {
					log.debug("Closing window and saving properties");
					PropertiesModel.getInstance().save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {}
			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
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
		if (playerManager.existsHumanPlayer()) {
			panel.add(getStartPanel(play));
			panel.add(play);
		}
		panel.add(getDirectionalPanel());
		panel.add(getZoomPanel());
		return panel;
	}
	
	private JPanel getStartPanel(final JButton play) {
		final JPanel panel = new JPanel(new GridLayout(mapManager.getStartPosition().size(), 1));
		final JRadioButton[] solution = new JRadioButton[mapManager.getStartPosition().size()];
		playerManager.initStartPosition();
		final List<Position> positionList = new ArrayList<Position>(mapManager.getStartPosition());
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < MapManager.ROAD_SIZE; i++) {
			JRadioButton box = new JRadioButton("");
			box.setEnabled(false);
			box.setSelected(false);
			group.add(box);
			solution[i] = box;
			panel.add(box);
		}
		for (int i = 0; i < positionList.size(); ) {
			Position p = positionList.get(i);
			if (mapManager.getCase(p.getY(), p.getX()).isOccuped()) {
				positionList.remove(i);
			} else {
				solution[i].setText("( " + Integer.toString(p.getX()) + " , " + Integer.toString((mapManager.getMapSize() - p.getY()) - 1) + " )");
				solution[i].setEnabled(true);
				i++;
			}
		}
		play.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = getSelectedCheckBox(solution);
				if (selected == -1) {
					return;
				}
				
				playerManager.updatePlayer(playerManager.getCurrentPlayer(), selected);
				if (playerManager.initStartPosition()) {
					displayMessage(playerManager.getCurrentPlayer().toString());
					for (int i = 0; i < positionList.size(); ) {
						Position p = positionList.get(i);
						if (mapManager.getCase(p.getY(), p.getX()).isOccuped()) {
							positionList.remove(i);
						} else {
							solution[i].setText("( " + Integer.toString(p.getX()) + " , " + Integer.toString((mapManager.getMapSize() - p.getY()) - 1) + " )");
							solution[i].setEnabled(true);
							i++;
						}
					}
					for (int i = positionList.size(); i < MapManager.ROAD_SIZE; i++) {
						solution[i].setText("");
						solution[i].setEnabled(false);
					}
				} else {
					removeButtonListener(play);
					panel.removeAll();
					getFirstStepPanel(play, panel);
				}
				
			}
		});
		return panel;
	}
	
	private void getFirstStepPanel(final JButton play, final JPanel panel) {
		panel.setLayout(new GridLayout(2, 2));
		panel.add(new JLabel("x"));
		final JTextField xField = new JTextField();
		xField.addKeyListener(getKeyListener(xField));
		playerManager.initAIFirstMove();
		panel.add(xField);
		panel.add(new JLabel("y"));
		final JTextField yField = new JTextField();
		yField.addKeyListener(getKeyListener(yField));
		panel.add(yField);
		play.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!checkFirstMovingValues(xField, yField)) {
					displayErrorMessage("Values not correct");
					return;
				}
				int distance = (caseArrayList.size() - mapManager.getMapSize()) / 2;
				int xMoving = getValue(xField);
				int yMoving = getValue(yField);
				int xTrayPanel = playerManager.getCurrentPlayer().getPosition().getX() + distance;
				int yTrayPanel = playerManager.getCurrentPlayer().getPosition().getY() + distance;
				JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
				JCase c2 = caseArrayList.get(yTrayPanel - yMoving).get(xTrayPanel + xMoving);
				if (c2.getModel().isOccuped()) {
					displayErrorMessage("Player on it");
					return;
				}
				Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
				if (isGrassIntersection(line)) {
					displayErrorMessage("Your are in grass!!!!");
					return;
				}
				playerManager.getCurrentPlayer().getVector().setX(xMoving);
				playerManager.getCurrentPlayer().getVector().setY(yMoving);
				if (playerManager.initFirstMove(new Vector(xMoving, yMoving))) {
					displayMessage(playerManager.getCurrentPlayer().toString());
					xField.setText("");
					yField.setText("");
				} else {
					removeButtonListener(play);
					panel.removeAll();
					getChoicePanel(play, panel);
				}
			}
		});
		panel.validate();
	}
	
	private KeyListener getKeyListener(final JTextField textField) {
		return new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				Pattern pattern = Pattern.compile("-{0,1}[\\d]+");
				if (pattern.matcher(textField.getText()).matches()) {
					textField.setForeground(Color.GREEN);
				} else {
					textField.setForeground(Color.RED);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {}
		};	
	}
	
	private boolean checkFirstMovingValues(JTextField xTextField, JTextField yTextField) {
		Pattern pattern = Pattern.compile("-{0,1}[\\d]+");
		Matcher xMatcher = pattern.matcher(xTextField.getText());
		Matcher yMatcher = pattern.matcher(yTextField.getText());
		return xMatcher.matches() && yMatcher.matches();
	}
	
	private int getValue(JTextField xTextField) {
		if ("".equals(xTextField)) {
			return 0;
		}
		return Integer.parseInt(xTextField.getText());
	}
	
	private void getChoicePanel(final JButton play, final JPanel panel) {
		playerManager.AIPlay();
		panel.setLayout(new GridLayout(5, 1));
		final JCheckBox[] solution = new JCheckBox[5];
		final List<Vector> vectorList = playerManager.getVectorsPossibilities(playerManager.getCurrentPlayer());
		for (Vector v : vectorList) {
			log.debug(v.toString());
		}
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < 5; i++) {
			JCheckBox box = new JCheckBox("");
			box.setEnabled(false);
			box.setSelected(false);
			group.add(box);
			solution[i] = box;
			panel.add(box);
		}
		
		int distance = (caseArrayList.size() - mapManager.getMapSize()) / 2;
		int xTrayPanel = playerManager.getCurrentPlayer().getPosition().getX() + distance;
		int yTrayPanel = playerManager.getCurrentPlayer().getPosition().getY() + distance;
		for (int i = 0; i < vectorList.size();) {
			Vector v = vectorList.get(i);
			JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
			JCase c2 = caseArrayList.get(yTrayPanel - v.getY()).get(xTrayPanel + v.getX());
			log.debug("solution : {}", vectorList.get(i).toString());
			
			Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
			if (isGrassIntersection(line)) {
				vectorList.remove(i);
			} else {
				solution[i].setEnabled(true);
				log.debug("Solution displayed : {}", vectorList.get(i).toString());
				solution[i].setText("( " + vectorList.get(i).getX() + ", " + vectorList.get(i).getY() + " )");
				i++;
			}
		}
		for (int i = vectorList.size(); i < 5; i++) {
			solution[i].setText("");
			solution[i].setEnabled(false);
			solution[i].setSelected(false);
		}
		
		playerManager.addUpdateCaseListener(new UpdateCaseListener() {
			
			@Override
			public void updatePlayerPossibilities(Player player) {
				vectorList.clear();
				vectorList.addAll(playerManager.getVectorsPossibilities(player));
				for (Vector v : vectorList) {
					log.debug(v.toString());
				}
				int distance = (caseArrayList.size() - mapManager.getMapSize()) / 2;
				int xTrayPanel = player.getPosition().getX() + distance;
				int yTrayPanel = player.getPosition().getY() + distance;
				for (int i = 0; i < vectorList.size();) {
					Vector v = vectorList.get(i);
					JCase c = caseArrayList.get(yTrayPanel).get(xTrayPanel);
					JCase c2 = caseArrayList.get(yTrayPanel - v.getY()).get(xTrayPanel + v.getX());
					
					Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
					if (isGrassIntersection(line)) {
						log.debug("solution removed : {}", vectorList.get(i).toString());
						vectorList.remove(i);
					} else {
						log.debug("solution displayed : {}", vectorList.get(i).toString());
						i++;
					}
				}
				if (vectorList.isEmpty()) {
					displayErrorMessage("You lose\nNo possibility to play!!!");
					//System.exit(0);
				}
				for (int i = 0; i < vectorList.size(); i++) {
					Vector v = vectorList.get(i);
					if (v != null) {
						solution[i].setText("( " + v.getX() + ", " + v.getY() + " )");
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
				if (playerManager.getPlayerList().size() > 1) {
					displayMessage(playerManager.getCurrentPlayer().getName());
				}
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
				JCase c2 = caseArrayList.get(player.getPosition().getY() - vector.getY() + distance).get(player.getPosition().getX() + vector.getX() + distance);
				
				Shape line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
				
				if (isEndLineIntersection(line)) {
					displayErrorMessage("end");
					playerManager.lastPlay(vector);
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
		JButton up = new JButton("↑");
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getVerticalScrollBar().getModel().setValue(scrollPane.getVerticalScrollBar().getModel().getValue() - 40);
			}
		});
		JButton down = new JButton("↓");
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getVerticalScrollBar().getModel().setValue(scrollPane.getVerticalScrollBar().getModel().getValue() + 40);
			}
		});
		JButton left = new JButton("←");
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getHorizontalScrollBar().getModel().setValue(scrollPane.getHorizontalScrollBar().getModel().getValue() - 40);
			}
		});
		JButton right = new JButton("→");
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollPane.getHorizontalScrollBar().getModel().setValue(scrollPane.getHorizontalScrollBar().getModel().getValue() + 40);
			}
		});
		
		JButton centered = new JButton("center");
		centered.addActionListener(getPlayerFocusListener());
		panel.add(up, BorderLayout.NORTH);
		panel.add(left, BorderLayout.WEST);
		panel.add(right, BorderLayout.EAST);
		panel.add(down, BorderLayout.SOUTH);
		panel.add(centered, BorderLayout.CENTER);
		return panel;
	}
	
	private ActionListener getPlayerFocusListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Position pos = playerManager.getCurrentPlayer().getPosition();
				double d = mapManager.getMapSize() / 100;
				double x = d * (pos.getX() - 20);
				double y = d * (pos.getY() - 20);
				scrollPane.getHorizontalScrollBar().getModel().setValue(new Double(x * scrollPane.getHorizontalScrollBar().getModel().getMaximum() / 100).intValue());
				scrollPane.getVerticalScrollBar().getModel().setValue(new Double(y * scrollPane.getVerticalScrollBar().getModel().getMaximum() / 100).intValue());
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
	
	private int getSelectedCheckBox(JRadioButton[] radioButtonArray) {
		for (int i = 0; i < 4; i++) {
			if (radioButtonArray[i].isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean isGrassIntersection(Shape shape) {
		return checkIntersection(shape, Field.HERBE);
	}
	
	private boolean isEndLineIntersection(Shape shape) {
		return checkIntersection(shape, Field.END_LINE);
	}
	
	private boolean checkIntersection(Shape shape, Field terrain) {
		for (List<JCase> caseList : caseArrayList) {
			for (JCase c : caseList) {
				if (c.getModel() != null && c.getModel().getField() == terrain) {
					if (shape.intersects(c.getX(), c.getY(), c.getWidth(), c.getHeight())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(getFileMenu());
		menuBar.add(getConfigurationMenu());
		menuBar.add(getHelp());
		return menuBar;
	}
	
	private JMenu getFileMenu() {
		JMenu file = new JMenu("File");
		return file;
	}
	
	private JMenu getConfigurationMenu() {
		JMenu file = new JMenu("Configuration");
		return file;
	}
	
	private JMenu getHelp() {
		JMenu help = new JMenu("Help");
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AboutFrame.getNewInstance(mainFrame);
			}
		});
		help.add(about);
		return help;
	}

	private void displayErrorMessage(String msg) {
		JOptionPane.showMessageDialog(mainFrame, msg, NAME + " " + VERSION + " - ERROR!", JOptionPane.ERROR_MESSAGE);
	}
	
	private void displayMessage(String msg) {
		JOptionPane.showMessageDialog(mainFrame, msg, NAME + " " + VERSION, JOptionPane.INFORMATION_MESSAGE);
	}
}
