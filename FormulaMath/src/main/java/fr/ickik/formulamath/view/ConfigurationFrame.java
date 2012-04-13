package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;

/**
 * This frame helps the user to configure the game.
 * The user can choose between Human and Computer players;
 * give a name to every player.
 * @author Ickik.
 * @version 0.1.003, 13 apr. 2012
 */
public class ConfigurationFrame {

	private final JFrame configurationFrame;
	//Can be changed in Toggle button to use less memory
	private final List<List<JRadioButton>> radioButtonPlayerTypeList = new ArrayList<List<JRadioButton>>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	//private final List<JToggleButton> togglePlayerTypeList = new ArrayList<JToggleButton>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final List<JTextField> nameTextFieldList = new ArrayList<JTextField>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final List<JLabel> labelList = new ArrayList<JLabel>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final Logger log = LoggerFactory.getLogger(ConfigurationFrame.class);
	private int numberOfPlayerSelected = 1;
	
	/**
	 * Default constructor. Creates the frame to configure the game.
	 */
	public ConfigurationFrame() {
		configurationFrame = new JFrame(MainFrame.NAME + " " + MainFrame.VERSION);
		createMainFrame();
		centeredFrame(configurationFrame);
	}

	private void createMainFrame() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getConfigurationPanel(), BorderLayout.CENTER);
		panel.add(getNumberPlayerPanel(), BorderLayout.NORTH);
		File help = new File("./help.pdf");
		if (help.exists()) {
			log.debug("Help file exist");
			panel.add(getButtonHelp(), BorderLayout.SOUTH);
		} else {
			log.debug("Help file not found");
			panel.add(getButton(), BorderLayout.SOUTH);
		}
		configurationFrame.add(panel);
		configurationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		configurationFrame.pack();
		//Image icon = Toolkit.getDefaultToolkit().createImage(ConfigurationFrame.class.getResource("FormulaMath_icon2.png"));
		//configurationFrame.setIconImage(icon);
		configurationFrame.setVisible(true);
	}

	private JPanel getButton() {
		JPanel panel = new JPanel(new GridLayout(1, 3));
		JButton okButton = new JButton("OK");
		okButton.setMnemonic(KeyEvent.VK_O);
		configurationFrame.getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				configurationFrame.dispose();
				int max = 0;
				for (int i = 0; i < numberOfPlayerSelected; i++) {
					if (nameTextFieldList.get(i).isEnabled()) {
						max = i + 1;
					}
				}
				MapManager mapManager = new MapManager(100);
				PlayerManager pm = PlayerManager.getInstance();
				pm.setMapManager(mapManager);
				for (int i = 0; i < max; i++) {//Si aucun joueur n'est selectionné, par defaut il est CPU
					PlayerType type;
					if (radioButtonPlayerTypeList.get(i).get(0).isSelected()) {
						type = PlayerType.HUMAN;
					} else {
						type = PlayerType.COMPUTER;
					}
					pm.addPlayer(new Player(type, nameTextFieldList.get(i).getText()));
				}
				new MainFrame(pm, mapManager);
			}
		});
		JButton cancelAndQuit = new JButton("Cancel & Quit");
		cancelAndQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				configurationFrame.dispose();
				System.exit(0);
			}
		});
		panel.add(okButton);
		panel.add(cancelAndQuit);
		return panel;
	}

	private JPanel getButtonHelp() {
		JPanel panel = new JPanel(new GridBagLayout());
		JButton okButton = new JButton("Ok");
		okButton.setMnemonic(KeyEvent.VK_O);
		configurationFrame.getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				configurationFrame.dispose();
				int length = nameTextFieldList.size();
				int max = 0;
				for (int i = 0; i < length; i++) {
					if (nameTextFieldList.get(i).isEnabled()) {
						max = i + 1;
					}
				}
				MapManager mapManager = new MapManager(100);
				PlayerManager pm = PlayerManager.getInstance();
				pm.setMapManager(mapManager);
				for (int i = 0; i < max; i++) {
					PlayerType type = PlayerType.COMPUTER;
					if (radioButtonPlayerTypeList.get(i).get(0).isSelected()) {
						type = PlayerType.HUMAN;
					}
					pm.addPlayer(new Player(type, nameTextFieldList.get(i).getText()));
				}
				new MainFrame(pm, mapManager);
			}
		});
		JButton cancelAndQuit = new JButton("Cancel & Quit");
		cancelAndQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				configurationFrame.dispose();
				System.exit(0);
			}
		});
		JButton helpButton = new JButton("help");
		helpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openHelpFile();
			}
		});
		GridBagConstraints grid = new GridBagConstraints();
		grid.fill = GridBagConstraints.HORIZONTAL;
		grid.gridx = 0;
		grid.gridwidth = 2;
		grid.weightx=1;
		panel.add(okButton, grid);
		grid.fill = GridBagConstraints.HORIZONTAL;
		grid.gridx = 2;
		grid.gridwidth = 2;
		grid.weightx=1;
		panel.add(cancelAndQuit, grid);
		grid.fill = GridBagConstraints.HORIZONTAL;
		grid.weightx=0.5;
		grid.gridx = 4;
		panel.add(helpButton, grid);
		return panel;
	}
	
	private void openHelpFile() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().open(new File("./help.pdf"));
			} catch (IOException e) {
				log.error("Help file not found or corrupted! {}", e.getMessage());
				displayErrorMessage(e.getMessage());
			}
		}
	}
	
	private void displayErrorMessage(String msg) {
		JOptionPane.showMessageDialog(configurationFrame, msg, MainFrame.getTitle() + " - ERROR!", JOptionPane.ERROR_MESSAGE);
	}
	
	private JPanel getNumberPlayerPanel() {
		GridLayout gridLayout = new GridLayout(1, 2);
		JPanel panel = new JPanel(gridLayout);

		JLabel label = new JLabel("Number of player : ");
		String[] nbPlayer = { "1", "2", "3", "4" };
		final JComboBox<String> comboBox = new JComboBox<String>(nbPlayer);
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				numberOfPlayerSelected = comboBox.getSelectedIndex() + 1;
				for (int i = 0; i < numberOfPlayerSelected; i++) {
					radioButtonPlayerTypeList.get(i).get(0).setEnabled(true);
					radioButtonPlayerTypeList.get(i).get(1).setEnabled(true);
					labelList.get(i).setEnabled(true);
					nameTextFieldList.get(i).setEnabled(true);
				}
				for (int i = numberOfPlayerSelected; i < PlayerManager.NUMBER_OF_PLAYER_MAX; i++) {
					radioButtonPlayerTypeList.get(i).get(0).setEnabled(false);
					radioButtonPlayerTypeList.get(i).get(1).setEnabled(false);
					labelList.get(i).setEnabled(false);
					nameTextFieldList.get(i).setEnabled(false);
				}
			}
		});
		//comboBox.setSelectedIndex(0);
		panel.add(label);
		panel.add(comboBox);
		return panel;
	}

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		/*GridLayout gridLayout = new GridLayout(PlayerManager.NUMBER_OF_PLAYER_MAX, 3);
		panel.setLayout(gridLayout);
		for (int i = 0; i < PlayerManager.NUMBER_OF_PLAYER_MAX; i++) {
			final JToggleButton button = new JToggleButton("Computer");
			button.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent arg0) {
					if (button.isSelected()) {
						button.setText("Human");
					} else {
						button.setText("Computer");
					}
				}
			});
			panel.add(button);
			JLabel lbl = new JLabel("Name : ");
			panel.add(lbl);
			labelList.add(lbl);
			JTextField name = new JTextField();
			nameTextFieldList.add(name);
			panel.add(name);
			if (i != 0) {
				button.setEnabled(false);
				lbl.setEnabled(false);
				name.setEnabled(false);
			}
		}*/
		
		GridLayout gridLayout = new GridLayout(PlayerManager.NUMBER_OF_PLAYER_MAX, 4);
		panel.setLayout(gridLayout);
		for (int i = 0; i < PlayerManager.NUMBER_OF_PLAYER_MAX; i++) {
			JRadioButton human = new JRadioButton("Human");
			JRadioButton computer = new JRadioButton("Computer");
			ButtonGroup group = new ButtonGroup();
			List<JRadioButton> radioButtonList = new ArrayList<JRadioButton>(2);
			group.add(human);
			radioButtonList.add(human);
			group.add(computer);
			radioButtonList.add(computer);
			radioButtonPlayerTypeList.add(radioButtonList);
			panel.add(human);
			panel.add(computer);
			JLabel lbl = new JLabel("Name : ");
			panel.add(lbl);
			labelList.add(lbl);
			JTextField name = new JTextField();
			nameTextFieldList.add(name);
			panel.add(name);
			if (i != 0) {
				human.setEnabled(false);
				computer.setEnabled(false);
				lbl.setEnabled(false);
				name.setEnabled(false);
			}
		}
		return panel;
	}
	
	private void centeredFrame(JFrame frame) {
		double w = frame.getWidth();
		double h = frame.getHeight();
		double l = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double l2 = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		frame.setLocation((int) (l / 2 - w / 2), (int)(l2 / 2 - h / 2));
	}
}
