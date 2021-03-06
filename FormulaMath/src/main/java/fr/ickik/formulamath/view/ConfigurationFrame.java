package fr.ickik.formulamath.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.controler.FormulaMathController;
import fr.ickik.formulamath.model.FormulaMathProperty;
import fr.ickik.formulamath.model.JTextFieldLimit;
import fr.ickik.formulamath.model.PropertiesModel;
import fr.ickik.formulamath.model.ai.AILevelFactory;
import fr.ickik.formulamath.model.map.MapDimension;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;

/**
 * This frame helps the user to configure the game.
 * The user can choose between Human and Computer players;
 * give a name to every player.
 * @author Ickik.
 * @version 0.2.012, 11th February 2013.
 */
public class ConfigurationFrame extends AbstractFormulaMathFrame {

	private final List<JToggleButton> togglePlayerTypeList = new ArrayList<JToggleButton>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final List<JTextField> nameTextFieldList = new ArrayList<JTextField>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final List<JLabel> labelList = new ArrayList<JLabel>(PlayerManager.NUMBER_OF_PLAYER_MAX);
	private final Logger log = LoggerFactory.getLogger(ConfigurationFrame.class);
	private int numberOfPlayerSelected = 1;
	private int dimensionMapItem = 1;
	//private File selectedFileMap = null;
	private final FormulaMathProperty[] namePropertyArray = {FormulaMathProperty.PLAYER1_NAME, FormulaMathProperty.PLAYER2_NAME, 
			FormulaMathProperty.PLAYER3_NAME, FormulaMathProperty.PLAYER4_NAME};
	private final FormulaMathProperty[] typePropertyArray = {FormulaMathProperty.PLAYER1_TYPE, FormulaMathProperty.PLAYER2_TYPE, 
			FormulaMathProperty.PLAYER3_TYPE, FormulaMathProperty.PLAYER4_TYPE};
	private int computerLevel = 1;
	private final FormulaMathController controller;

	/**
	 * Default constructor. Creates the frame to configure the game.
	 */
	public ConfigurationFrame(FormulaMathController controller) {
		this.controller = controller;
		createMainFrame();
	}
	
	public void display() {
		displayFrame();
		getFrame().setResizable(false);
		getFrame().toFront();
	}

	private void createMainFrame() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(getConfigurationPanel(), BorderLayout.CENTER);
		panel.add(getNumberPlayerPanel(), BorderLayout.NORTH);
		File help = new File(System.getProperty("user.home") + "/.FormulaMath/help.pdf");
		if (help.exists()) {
			log.debug("Help file exist");
			panel.add(getButtonHelp(), BorderLayout.SOUTH);
		} else {
			log.debug("Help file not found");
			panel.add(getButton(), BorderLayout.SOUTH);
		}
		if (getFrame().getWindowListeners().length == 0) {
			getFrame().addWindowListener(new WindowListener() {

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
		}
		getFrame().add(panel);
	}

	private JPanel getButton() {
		JPanel panel = new JPanel(new GridLayout(1, 3));
		JButton okButton = new JButton("OK");
		okButton.setMnemonic(KeyEvent.VK_O);
		getFrame().getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(getOKActionListener());
		JButton cancelAndQuit = new JButton("Cancel & Quit");
		cancelAndQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					controller.saveProperties();
				} catch (FormulaMathException e1) {
					log.error(e1.getMessage());
				} finally {
					close();
					System.exit(0);
				}
			}
		});
		panel.add(cancelAndQuit);
		panel.add(okButton);
		return panel;
	}
	
	private ActionListener getOKActionListener() {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				if (!isHumanPlayer()) {
					displayErrorMessage("No human player selected");
					return;
				}
				
				if (dimensionMapItem < MapDimension.values().length) {
					controller.initManager(MapDimension.values()[dimensionMapItem].getValue(), computerLevel);
				} else {
					/*if (selectedFileMap != null) {
						try {
							controller.loadMap(selectedFileMap);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						displayErrorMessage("No map file selected");
						return;
					}*/
				}
				log.trace("number of player selected : {}", numberOfPlayerSelected);
				
				for (int i = 0; i < numberOfPlayerSelected; i++) {
					PlayerType type = getPlayerType(i);
					String name =  getName(i);
					controller.addPlayer(type, name);
				}
				controller.closeConfigurationFrame();
			}
		};
	}
	
	private PlayerType getPlayerType(int index) {
		if (togglePlayerTypeList.get(index).isSelected()) {
			PropertiesModel.getSingleton().put(typePropertyArray[index], Boolean.TRUE.toString());
			return PlayerType.HUMAN;
		}
		PropertiesModel.getSingleton().put(typePropertyArray[index], Boolean.FALSE.toString());
		return PlayerType.COMPUTER;
	}
	
	private String getName(int index) {
		String name =  nameTextFieldList.get(index).getText();
		if (name != null | !name.isEmpty()) {
			PropertiesModel.getSingleton().put(namePropertyArray[index], name);
		}
		return name;
	}
	
	private boolean isHumanPlayer() {
		int nbHuman = 0;
		for (int i = 0; i < numberOfPlayerSelected; i++) {
			if (togglePlayerTypeList.get(i).isSelected()) {
				nbHuman++;
			}
		}
		if (nbHuman == 0) {
			return false;
		}
		return true;
	}

	private JPanel getButtonHelp() {
		//JPanel panel = new JPanel(new GridBagLayout());
		JPanel panel = new JPanel(new GridLayout(1,3));
		JButton okButton = new JButton("OK");
		//JButton okButton = new ValidationButton("OK");

		okButton.setMnemonic(KeyEvent.VK_O);
		getFrame().getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(getOKActionListener());
		JButton cancelAndQuit = new JButton("Cancel & Quit");
		cancelAndQuit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				close();
				System.exit(0);
			}
		});
		JButton helpButton = new JButton("help");
		helpButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					controller.openHelpFile();
				} catch (FormulaMathException exception) {
					log.error("Help file not found or corrupted : {}", exception.getMessage());
					displayErrorMessage(exception.getMessage());
				}
			}
		});
		panel.add(cancelAndQuit);
		panel.add(helpButton);
		panel.add(okButton);
		/*GridBagConstraints grid = new GridBagConstraints();
		grid.fill = GridBagConstraints.HORIZONTAL;
		grid.gridx = 0;
		grid.gridwidth = 1;
		//grid.weightx=1;
		panel.add(cancelAndQuit, grid);
		grid.fill = GridBagConstraints.HORIZONTAL;
		grid.gridx = 2;
		grid.gridwidth = 2;
		//grid.weightx=1;
		panel.add(helpButton, grid);
		grid.fill = GridBagConstraints.HORIZONTAL;
		grid.weightx=1;
		grid.gridwidth = 2;
		grid.gridx = 4;
		panel.add(okButton, grid);*/
		return panel;
	}
	
	private JPanel getNumberPlayerPanel() {
		GridLayout gridLayout = new GridLayout(3, 2);
		JPanel panel = new JPanel(gridLayout);

		final JComboBox<String> sizeComboBox = new JComboBox<String>();
		for (MapDimension d : MapDimension.values()) {
			sizeComboBox.addItem(d.toString());
		}
	//	sizeComboBox.addItem("Choose a map");
		sizeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
//				int previous = dimensionMapItem;
				dimensionMapItem = sizeComboBox.getSelectedIndex();
//				if (dimensionMapItem == sizeComboBox.getItemCount() - 1) {
//					JFileChooser fileChooser = new JFileChooser();
//					fileChooser.setMultiSelectionEnabled(false);
//					fileChooser.setFileFilter(new FileFilter() {
//						
//						@Override
//						public String getDescription() {
//							return "FMS saved file";
//						}
//						
//						@Override
//						public boolean accept(File arg0) {
//							return arg0.getName().endsWith(".fms");
//						}
//					});
//					int result = fileChooser.showOpenDialog(getFrame());
//					if (result == JFileChooser.APPROVE_OPTION) {
//						selectedFileMap = fileChooser.getSelectedFile();
//					} else {
//						dimensionMapItem = previous;
//						sizeComboBox.setSelectedIndex(dimensionMapItem);
//					}
//				}
			}
		});
		sizeComboBox.setSelectedIndex(dimensionMapItem);
		
		final JComboBox<String> levelComboBox = new JComboBox<String>(AILevelFactory.LEVEL);
		levelComboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				computerLevel = levelComboBox.getSelectedIndex();
			}
		});
		levelComboBox.setSelectedIndex(computerLevel);
		
		JLabel label = new JLabel("Number of player : ");
		String[] nbPlayer = { "1", "2", "3", "4" };
		final JComboBox<String> comboBox = new JComboBox<String>(nbPlayer);
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				numberOfPlayerSelected = comboBox.getSelectedIndex() + 1;
				for (int i = 0; i < numberOfPlayerSelected; i++) {
					togglePlayerTypeList.get(i).setEnabled(true);
					labelList.get(i).setEnabled(true);
					nameTextFieldList.get(i).setEnabled(true);
				}
				for (int i = numberOfPlayerSelected; i < PlayerManager.NUMBER_OF_PLAYER_MAX; i++) {
					togglePlayerTypeList.get(i).setEnabled(false);
					labelList.get(i).setEnabled(false);
					nameTextFieldList.get(i).setEnabled(false);
				}
			}
		});
		
		panel.add(new JLabel("Computer level : "));
		panel.add(levelComboBox);
		panel.add(new JLabel("Dimension of the map : "));
		panel.add(sizeComboBox);
		panel.add(label);
		panel.add(comboBox);
		return panel;
	}
	
//	private class ComboBoxRenderer extends JLabel implements ListCellRenderer<String> {
//
//		public ComboBoxRenderer() {
//			setOpaque(true);
//			setHorizontalAlignment(CENTER);
//			setVerticalAlignment(CENTER);
//		}
//
//		@Override
//		public Component getListCellRendererComponent(
//				JList<? extends String> list, String value, int index,
//				boolean isSelected, boolean cellHasFocus) {
//
//			//always valid, so just use the value.)
//			int selectedIndex = index;
//
//			if (isSelected) {
//				setBackground(list.getSelectionBackground());
//				setForeground(list.getSelectionForeground());
//			} else {
//				setBackground(list.getBackground());
//				setForeground(list.getForeground());
//			}
//
//			/*ImageIcon icon = images[selectedIndex];
//            String pet = petStrings[selectedIndex];
//            setIcon(icon);
//            if (icon != null) {
//                setText(pet);
//            } else {
//                setUhOhText(pet + " (no image available)");
//            }
//			 */
//			return this;
//		}
//
//		private void setUhOhText(String uhOhText) {
//			setText(uhOhText);
//		}
//
//	}

	private JPanel getConfigurationPanel() {
		JPanel panel = new JPanel();
		GridLayout gridLayout = new GridLayout(PlayerManager.NUMBER_OF_PLAYER_MAX, 3);
		panel.setLayout(gridLayout);
		
		for (int i = 0; i < PlayerManager.NUMBER_OF_PLAYER_MAX; i++) {
			JToggleButton button = toggleButtonFactory();
			togglePlayerTypeList.add(button);
			panel.add(button);
			JLabel lbl = new JLabel("Name : ", SwingConstants.CENTER);
			panel.add(lbl);
			labelList.add(lbl);
			JTextField name = new JTextField(new JTextFieldLimit(20), PropertiesModel.getSingleton().getProperty(namePropertyArray[i]), 0);
			nameTextFieldList.add(name);
			panel.add(name);
			button.setSelected(Boolean.parseBoolean(PropertiesModel.getSingleton().getProperty(typePropertyArray[i])));
			if (i != 0) {
				button.setEnabled(false);
				lbl.setEnabled(false);
				name.setEnabled(false);
			}
		}
		return panel;
	}
	
	private JToggleButton toggleButtonFactory() {
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
		return button;
	}

}
