package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.controler.ChuckNorrisListener;
import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.controler.UpdateCaseListener;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.CaseModel;

/**
 * This class create the main frame of the application.
 * @author Ickik.
 * @version 0.2.001, 14 mai 2012.
 */
public final class MainFrame extends AbstractFormulaMathFrame implements ChuckNorrisListener, UpdateCaseListener {

	private final JFrame mainFrame;
	private final JPanel gameMenuPanel;
	private final JButton playButton;
	private int caseSize = 15;
	private final StartPositionChooserPanel startPositionChooserPanel;
	private final PlayVectorChooserPanel playVectorChooserPanel;
	private final FirstMovePanel firstMovePanel;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private JScrollPane scrollPane;
	private List<List<JCase>> caseArrayList;
	private static final int MIN_ZOOM_SIZE = 10;
	private static final int MAX_ZOOM_SIZE = 50;
	private final FormulaMathController controller;
	public static final int MAP_MARGIN = 20;
	private final String theme;
	
	public MainFrame(int mapSize, FormulaMathController controller, String theme) {
		this.controller = controller;
		this.theme = theme;
		mainFrame = getFrame();
		gameMenuPanel = new JPanel();
		playButton = new JButton("Play");
		startPositionChooserPanel = new StartPositionChooserPanel(gameMenuPanel, playButton, controller);
		firstMovePanel = new FirstMovePanel(gameMenuPanel, playButton, controller, mainFrame);
		playVectorChooserPanel = new PlayVectorChooserPanel(gameMenuPanel, playButton, controller);
		caseArrayList = new ArrayList<List<JCase>>(mapSize + MAP_MARGIN);
	}
	
	public void display(List<List<CaseModel>> carte) {
		initMap(carte);
		createMainFrame();
		controller.chooseStartPosition();
	}
	
	private void initMap(List<List<CaseModel>> carte) {
		int sideSize = carte.size() + MAP_MARGIN;
		int x = 0;
		int y = 0;
		int endOfMapIndex = sideSize - (MAP_MARGIN / 2);
		int marge = MAP_MARGIN / 2;
		for (int i = 0; i < sideSize; i++) {
			x = 0;
			List<JCase> caseList = new ArrayList<JCase>(sideSize);
			for (int j = 0; j < sideSize; j++) {
				if (i >= marge && i < endOfMapIndex && j >= marge && j < endOfMapIndex) {
					caseList.add(new JCase(caseSize, carte.get(y).get(x)));
					x++;
				} else {
					caseList.add(new JCase(caseSize, null));
				}
			}
			caseArrayList.add(caseList);
			if (i >= marge && i <= (sideSize - marge)) {
				y++;
			}
		}
	}

	private void createMainFrame() {
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.add(getSplitPane(), BorderLayout.CENTER);
		mainFrame.setJMenuBar(getMenuBar());
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
					controller.saveProperties();
				} catch (FormulaMathException e) {
					displayErrorMessage(e.getMessage());
				}
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {}
			
			@Override
			public void windowActivated(WindowEvent arg0) {}
		});
		displayFrame();
	}

	private JSplitPane getSplitPane() {
		JPanel trayPanel = getTrayPanel();
		scrollPane = new JScrollPane(trayPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
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
		panel.add(gameMenuPanel);
		panel.add(playButton);
		panel.add(getDirectionalPanel());
		panel.add(getZoomPanel());
		return panel;
	}

	private JPanel getZoomPanel() {
		JPanel zoomPanel = new JPanel(new GridLayout(1, 2));
		JButton zoom = new JButton("+");
		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (caseSize < MAX_ZOOM_SIZE) {
					caseSize++;
					log.trace("Zoom : {}", caseSize);
					repaintTrayPanel();
				}
			}
		});
		JButton dezoom = new JButton("-");
		dezoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (caseSize > MIN_ZOOM_SIZE) {
					caseSize--;
					log.trace("Dezoom : {}", caseSize);
					repaintTrayPanel();
				}
			}
		});
		zoomPanel.add(zoom);
		zoomPanel.add(dezoom);
		return zoomPanel;
	}

	private JPanel getDirectionalPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 3));
		JButton up = new JButton("↑");
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				log.trace("Up move");
				scrollPane.getVerticalScrollBar().getModel().setValue(scrollPane.getVerticalScrollBar().getModel().getValue() - 40);
			}
		});
		JButton down = new JButton("↓");
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				log.trace("Down move");
				scrollPane.getVerticalScrollBar().getModel().setValue(scrollPane.getVerticalScrollBar().getModel().getValue() + 40);
			}
		});
		JButton left = new JButton("←");
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				log.trace("Left move");
				scrollPane.getHorizontalScrollBar().getModel().setValue(scrollPane.getHorizontalScrollBar().getModel().getValue() - 40);
			}
		});
		JButton right = new JButton("→");
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				log.trace("Right move");
				scrollPane.getHorizontalScrollBar().getModel().setValue(scrollPane.getHorizontalScrollBar().getModel().getValue() + 40);
			}
		});
		JButton centered = new JButton("☼");
		centered.addActionListener(getPlayerFocusListener());
		panel.add(enabledButtonFactory());
		panel.add(up);
		panel.add(enabledButtonFactory());
		panel.add(left);
		panel.add(centered);
		panel.add(right);
		panel.add(enabledButtonFactory());
		panel.add(down);
		panel.add(enabledButtonFactory());
		return panel;
	}
	
	private JButton enabledButtonFactory() {
		JButton button = new JButton();
		button.setEnabled(false);
		return button;
	}
	
	private ActionListener getPlayerFocusListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double[] coordinateArray = controller.focusPlayerPosition(mainFrame.getSize());
				scrollPane.getHorizontalScrollBar().getModel().setValue(new Double(coordinateArray[0] * scrollPane.getHorizontalScrollBar().getModel().getMaximum() / 100).intValue());
				scrollPane.getVerticalScrollBar().getModel().setValue(new Double(coordinateArray[1] * scrollPane.getVerticalScrollBar().getModel().getMaximum() / 100).intValue());
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
	
	private JMenuBar getMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(getFileMenu());
		menuBar.add(getConfigurationMenu());
		menuBar.add(getHelp());
		return menuBar;
	}
	
	private JMenu getFileMenu() {
		JMenu file = new JMenu("File");
		JMenuItem save = new JMenuItem("Save Map");
		
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setMultiSelectionEnabled(false);
				int result = fileChooser.showSaveDialog(mainFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						controller.saveMap(fileChooser.getSelectedFile());
					} catch (IOException e) {
						log.error("The map could not be saved : {}", e.getMessage());
						displayErrorMessage("The map could not be saved");
					}
				}
			}
		});
		
		JMenuItem openMap = new JMenuItem("Load Map");
		openMap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setMultiSelectionEnabled(false);
				int result = fileChooser.showOpenDialog(mainFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						controller.loadMap(fileChooser.getSelectedFile());
					} catch (IOException e) {
						log.error("The map could not be loaded : {}", e.getMessage());
						displayErrorMessage("The map could not be loaded");
					}
				}
			}
		});
		
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.saveProperties();
				} catch (FormulaMathException e) {
					displayErrorMessage("Error during saving properties");
				}
				System.exit(0);
			}
		});
		//file.add(save);
		//file.add(loadMap);
		file.addSeparator();
		file.add(quit);
		return file;
	}
	
	private JMenu getConfigurationMenu() {
		JMenu file = new JMenu("Configuration");
		file.add(displayThemeMenu());
		return file;
	}
	
	private JMenu getHelp() {
		JMenu help = new JMenu("Help");
		JMenuItem guide = new JMenuItem("Guide");
		guide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.openHelpFile();
				} catch (FormulaMathException exception) {
					log.error("Help file not found or corrupted : {}", exception.getMessage());
					displayErrorMessage(exception.getMessage());
				}
			}
		});
		
		JMenuItem about = new JMenuItem("About");
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.openAboutFrame();
			}
		});
		
		help.add(guide);
		help.addSeparator();
		help.add(about);
		return help;
	}
	
	private JMenu displayThemeMenu() {
		JMenu item = new JMenu("Theme");
		ButtonGroup group = new ButtonGroup();
		for (final LookAndFeelInfo lnfInfo : UIManager.getInstalledLookAndFeels()) {
			JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(lnfInfo.getName());
			group.add(checkBox);
			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					controller.setLookAndFeel(lnfInfo.getClassName());
					SwingUtilities.updateComponentTreeUI(getFrame());
				}
			});
			if (theme.equals(lnfInfo.getClassName())) {
				checkBox.setSelected(true);
			}
			item.add(checkBox);
		}
		return item;
	}
	
	@Override
	public void updateTitle(String title) {
		getFrame().setTitle(title);
		getFrame().validate();
	}

	@Override
	public void updatePlayerCase() {
		mainFrame.repaint();
	}

	@Override
	public void updatePlayerPossibilities(Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayPlayerStartingPossibilities(Player player, List<Position> startingPositionList, int mapSize) {
		startPositionChooserPanel.construct(startingPositionList, mapSize);
		displayMessage("Player " + player.getName() + " (" + player.getId() + ") must choose the start position");
	}

	@Override
	public void displayPlayerFirstMove(Player player, int mapSize) {
		firstMovePanel.construct(caseArrayList, player.getPosition(), mapSize);
		displayMessage("Player " + player.getName() + " (" + player.getId() + ") must choose the first move");
	}
	
	@Override
	public void displayPlayerMovePossibilities(Player player, List<Vector> vectorList, int mapSize) {
		playVectorChooserPanel.construct(player, vectorList, mapSize, caseArrayList);
		displayMessage("Player " + player.getName() + " (" + player.getId() + ") must choose the next move");
	}

	@Override
	public void updateEndGamePanel() {
		displayMessage("message");
	}
}
