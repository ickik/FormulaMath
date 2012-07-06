package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
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
import fr.ickik.formulamath.model.FormulaMathMouseListener;
import fr.ickik.formulamath.model.InformationModel;
import fr.ickik.formulamath.model.map.MapDimension;

/**
 * This class create the main frame of the application.
 * @author Ickik.
 * @version 0.2.011, 06 July 2012.
 */
public final class MainFrame extends AbstractFormulaMathFrame implements ChuckNorrisListener, UpdateCaseListener {

	private final JFrame mainFrame;
	private final JPanel gameMenuPanel;
	private final JButton playButton;
	private final InformationPanel informationLabel;
	private int caseSize = 15;
	private final StartPositionChooserPanel startPositionChooserPanel;
	private final PlayVectorChooserPanel playVectorChooserPanel;
	private final FirstMovePanel firstMovePanel;
	private final FormulaMathMouseListener listener;
	private static final Logger log = LoggerFactory.getLogger(MainFrame.class);
	private JScrollPane scrollPane;
	private List<List<JCase>> caseArrayList;
	private static final int MAX_ZOOM_SIZE = 50;
	private final FormulaMathController controller;
	public static final int MAP_MARGIN = 20;
	private final String theme;

	/**
	 * Constructor of the JFrame, it initializes all panels and the map panel.
	 * @param mapSize the size of the map model.
	 * @param controller the controller of the application.
	 * @param theme the theme of the JFrame. The theme is depending the OS.
	 */
	public MainFrame(int mapSize, FormulaMathController controller, String theme, InformationModel informationModel) {
		this.controller = controller;
		this.theme = theme;
		mainFrame = getFrame();
		gameMenuPanel = new JPanel();
		playButton = new JButton("Play");
		listener = new FormulaMathMouseListener(caseSize);
		informationLabel = new InformationPanel();
		informationLabel.setInformationModel(informationModel);
		informationModel.addInformationMessageListener(informationLabel);
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
		long begin  = System.currentTimeMillis();
		int sideSize = carte.size() + MAP_MARGIN;
		int x = 0;
		int y = 0;
		int endOfMapIndex = sideSize - (MAP_MARGIN / 2);
		int marge = MAP_MARGIN / 2;
		if (carte.size() < MapDimension.MEDIUM.getValue()) {
			caseSize*=2;
			listener.setCaseSize(caseSize);
		}
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
		log.debug("Duration of map initialization : {}ms", (System.currentTimeMillis() - begin));
	}

	private void createMainFrame() {
		log.trace("Begin createMainFrame()");
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
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
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		log.trace("End createMainFrame()");
	}

	private JSplitPane getSplitPane() {
		JPanel trayPanel = getTrayPanel();
		scrollPane = new JScrollPane(trayPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(5);
		scrollPane.getVerticalScrollBar().setUnitIncrement(5);
		scrollPane.addMouseListener(listener);
		scrollPane.addMouseMotionListener(listener);
		listener.setScrollPane(scrollPane);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, getMenuPanel());
		split.setDividerLocation(new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8).intValue());
		return split;
	}

	private JPanel getTrayPanel() {
		log.debug("getTrayPanel");
		long begin = System.currentTimeMillis();
		GridLayout gridLayout = new GridLayout(caseArrayList.size(), caseArrayList.size());
		JPanel tray = new JPanel(gridLayout);
		tray.setOpaque(true);
		for (List<JCase> list : caseArrayList) {
			for (JCase c : list) {
				tray.add(c);
			}
		}
		log.debug("Duration of tray panel construction : {}ms", (System.currentTimeMillis() - begin));
		return tray;
	}

	private JPanel getMenuPanel() {
		JPanel menuPanel = new JPanel(new BorderLayout());
		menuPanel.add(informationLabel, BorderLayout.NORTH);
		JLabel descriptionLabel = new JLabel(" ");
		listener.setDescriptionLabel(descriptionLabel);
		descriptionLabel.setBorder(BorderFactory.createTitledBorder("Map information"));
		final JCheckBox gridVisible = new JCheckBox("Grid visible", true);
		gridVisible.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean isSelected = gridVisible.isSelected();
				for(List<JCase> list : caseArrayList) {
					for (JCase cas : list) {
						if (cas.getModel() != null) {
							cas.getModel().setPaintBorder(isSelected);
						}
					}
				}
				scrollPane.repaint();
			}
		});
		JPanel info = new JPanel(new GridLayout(2, 1));
		info.add(descriptionLabel);
		info.add(gridVisible);
		menuPanel.add(info, BorderLayout.SOUTH);
		final JPanel panel = new JPanel(new GridLayout(4, 1));
		panel.add(gameMenuPanel);
		panel.add(playButton);
		panel.add(getDirectionalPanel());
		panel.add(getZoomPanel());
		menuPanel.add(panel, BorderLayout.CENTER);
		return menuPanel;
	}
	
	private JPanel getZoomPanel() {
		JPanel zoomPanel = new JPanel(new GridLayout(1, 2));
		JButton zoom = new JButton("+");
		zoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (caseSize < MAX_ZOOM_SIZE) {
					caseSize++;
					listener.setCaseSize(caseSize);
					log.trace("Zoom : {}", caseSize);
					repaintTrayPanel();
				}
			}
		});
		JButton dezoom = new JButton("-");
		dezoom.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (caseSize * caseArrayList.size() > scrollPane.getWidth()) {
					caseSize--;
					listener.setCaseSize(caseSize);
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
		JButton up = new JButton(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/up.png")));
		up.setPressedIcon(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/up_pressed.png")));
		up.addActionListener(getDirectionButtonActionListener(up, scrollPane.getVerticalScrollBar().getModel(), -40));
		
		JButton down = new JButton(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/down.png")));
		down.setPressedIcon(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/down_pressed.png")));
		down.addActionListener(getDirectionButtonActionListener(down, scrollPane.getVerticalScrollBar().getModel(), 40));
		
		JButton left = new JButton(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/left.png")));
		left.setPressedIcon(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/left_pressed.png")));
		left.addActionListener(getDirectionButtonActionListener(left, scrollPane.getHorizontalScrollBar().getModel(), -40));
		
		JButton right = new JButton(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/right.png")));
		right.setPressedIcon(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/right_pressed.png")));
		right.addActionListener(getDirectionButtonActionListener(right, scrollPane.getHorizontalScrollBar().getModel(), 40));
		
		JButton centered = new JButton(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/center.png")));
		centered.setPressedIcon(new ImageIcon(AbstractFormulaMathFrame.class.getResource("img/center_pressed.png")));
		centered.addActionListener(getPlayerFocusListener());
		
		panel.add(disabledButtonFactory());
		panel.add(up);
		panel.add(disabledButtonFactory());
		panel.add(left);
		panel.add(centered);
		panel.add(right);
		panel.add(disabledButtonFactory());
		panel.add(down);
		panel.add(disabledButtonFactory());
		return panel;
	}
	
	private ActionListener getDirectionButtonActionListener(final JButton button, final BoundedRangeModel model, final int value) {
		final ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.setValue(model.getValue() + value);
			}
		};
		button.addMouseListener(getDirectionMouseListener(listener));
		return listener;
	}
	
	private ActionListener getDirectionButtonActionListener(final JButton button, final BoundedRangeModel hModel, final BoundedRangeModel vModel, final int value) {
		final ActionListener listener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				hModel.setValue(hModel.getValue() + value);
				vModel.setValue(vModel.getValue() + value);
			}
		};
		button.addMouseListener(getDirectionMouseListener(listener));
		return listener;
	}
	
	private MouseListener getDirectionMouseListener(final ActionListener listener) {
		return new MouseListener() {
			
			final Timer timer = new Timer(80, listener);
			@Override
			public void mouseReleased(MouseEvent arg0) {
				timer.stop();
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				timer.start();
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}
		};
	}
	
	private JButton disabledButtonFactory() {
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
//		mainFrame.validate();
		scrollPane.validate();
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
						if (!controller.saveMap(fileChooser.getSelectedFile())) {
							displayErrorMessage("An error was encounter during file saving");
						}
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
						try {
							controller.saveProperties();
						} catch (FormulaMathException ex) {
						} finally {
							System.exit(1);
						}
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
	public void displayPlayerStartingPossibilities(Player player, List<Position> startingPositionList, int mapSize) {
		startPositionChooserPanel.construct(startingPositionList, mapSize);
	}

	@Override
	public void displayPlayerFirstMove(Player player, int mapSize) {
		firstMovePanel.construct(caseArrayList, player.getPosition(), mapSize);
	}
	
	@Override
	public void displayPlayerMovePossibilities(Player player, List<Vector> vectorList, int mapSize) {
		log.debug("displayPlayerMovePossibilities for {}", player.toString());
		log.debug("Vector Possibilities size : {}", Integer.toString(vectorList.size()));
		playVectorChooserPanel.construct(player, vectorList, mapSize, caseArrayList);
	}

	@Override
	public void updateEndGamePanel() {
		controller.openStatFrame();
	}
}
