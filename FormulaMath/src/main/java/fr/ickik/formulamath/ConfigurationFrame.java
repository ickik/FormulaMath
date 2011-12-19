package fr.ickik.formulamath;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fr.ickik.formulamath.model.MapManager;
import fr.ickik.formulamath.model.PlayerManager;

/**
 * This frame helps the user to configure the game.
 * The user can choose between Human and Computer players;
 * give a name to every player.
 * @author Ickik.
 * @version 0.1.000, 1 sept. 2011
 */
public class ConfigurationFrame {

	private final JFrame configurationFrame;
	private final List<List<JRadioButton>> radioButtonPlayerTypeList = new ArrayList<List<JRadioButton>>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final List<JTextField> nameTextFieldList = new ArrayList<JTextField>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final List<JLabel> labelList = new ArrayList<JLabel>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	
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
		panel.add(getButton(), BorderLayout.SOUTH);
		configurationFrame.add(panel);
		configurationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		configurationFrame.pack();
		configurationFrame.setVisible(true);
	}

	private JPanel getButton() {
		JPanel panel = new JPanel(new GridLayout(1, 2));
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
		panel.add(okButton);
		panel.add(cancelAndQuit);
		return panel;
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
				for (int i = 0; i < comboBox.getSelectedIndex() + 1; i++) {
					radioButtonPlayerTypeList.get(i).get(0).setEnabled(true);
					radioButtonPlayerTypeList.get(i).get(1).setEnabled(true);
					labelList.get(i).setEnabled(true);
					nameTextFieldList.get(i).setEnabled(true);
				}
				for (int i = comboBox.getSelectedIndex() + 1; i < PlayerManager.NUMBER_OF_PLAYER_MAX; i++) {
					radioButtonPlayerTypeList.get(i).get(0).setEnabled(false);
					radioButtonPlayerTypeList.get(i).get(1).setEnabled(false);
					labelList.get(i).setEnabled(false);
					nameTextFieldList.get(i).setEnabled(false);
				}
			}
		});
		comboBox.setSelectedIndex(0);
		panel.add(label);
		panel.add(comboBox);
		return panel;
	}

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
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
