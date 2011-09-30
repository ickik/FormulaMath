package fr.ickik.formulamath;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.model.MapManager;
import fr.ickik.formulamath.model.PlayerManager;

public class StartFrame {

	private static final Logger log = LoggerFactory.getLogger(StartFrame.class);

	public StartFrame(final PlayerManager playerManager,
			final List<Position> listPosition, final BitSet bitSet,
			final Player p, final MapManager mapManager) {
		mapManager.display();
		final JFrame frame = new JFrame(MainFrame.NAME + " "
				+ MainFrame.VERSION);
		int nbPossibilities = listPosition.size() - bitSet.cardinality();
		log.debug("number of possibilities : {}", nbPossibilities);

		JPanel panel = new JPanel(new GridLayout(1, nbPossibilities + 1));
		final List<Integer> listPositionIndex = new ArrayList<Integer>(
				nbPossibilities);
		panel.add(new JLabel(p.getName() + "choose your start position : "));
		ButtonGroup group = new ButtonGroup();
		final JRadioButton[] radioButtonArray = new JRadioButton[nbPossibilities];
		int index = 0;
		for (int i = 0; i < nbPossibilities; i++) {
			while (bitSet.get(index)) {
				index++;
			}
			// log.debug("( " + listPosition.get(index).getX() + " , " +
			// listPosition.get(index).getY() + " )");
			JRadioButton radio = new JRadioButton(
					"( "
							+ listPosition.get(index).getX()
							+ " , "
							+ (mapManager.getMapSize()
									- listPosition.get(index).getY() - 1)
							+ " )");
			group.add(radio);
			radioButtonArray[i] = radio;
			listPositionIndex.add(index);
			panel.add(radio);
			index++;
		}
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				int selected = getSelectedRadioButton(radioButtonArray);
				if (selected == -1) {
					return;
				}
				p.getPosition().setX(
						listPosition.get(listPositionIndex.get(selected))
								.getX());
				p.getPosition().setY(
						listPosition.get(listPositionIndex.get(selected))
								.getY());
				bitSet.set(listPositionIndex.get(selected));
				frame.dispose();
				try {

					// mapManager.updateCase(listPosition.get(selected).getY(),
					// listPosition.get(selected).getX(), p);
					playerManager.updatePlayer(p, listPositionIndex.get(selected));
					playerManager.initStartPosition(p);
				} catch (FormulaMathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(button, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private int getSelectedRadioButton(JRadioButton[] radioButtonArray) {
		int len = radioButtonArray.length;
		for (int i = 0; i < len; i++) {
			if (radioButtonArray[i].isSelected()) {
				return i;
			}
		}
		return -1;
	}
}
