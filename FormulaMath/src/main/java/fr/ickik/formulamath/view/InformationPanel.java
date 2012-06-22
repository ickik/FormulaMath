package fr.ickik.formulamath.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import fr.ickik.formulamath.entity.InformationMessage;

/**
 * Information display in the menu. It display a pool of information.
 * @author Ickik.
 * @version 0.1.0, 22 June 2012
 * @since 0.3.4
 */
public class InformationPanel extends JLabel {

	private static final long serialVersionUID = 3510798157965794024L;
	private final Timer timer;
	private final int delay = 4000;
	private final List<InformationMessage> messageList = new ArrayList<InformationMessage>();
	
	public InformationPanel() {
		setBorder(BorderFactory.createTitledBorder("Information Panel"));
		timer = new Timer(delay, getTimerActionListener());
		timer.start();
		addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {timer.stop();}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {}
		});
	}
	
	private ActionListener getTimerActionListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final Timer t2 = new Timer(10, null);
				t2.addActionListener(new ActionListener() {
					int index=1;
					@Override
					public void actionPerformed(ActionEvent e) {
						String msg = messageList.get(0).getMessage();
						setText(msg.substring(0, index++));
						t2.stop();
					}
				});
				final Timer t = new Timer(10, null);
				t.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						String message = getText();
						int len = message.length();
						if (len < 1) {
							setText("");
							t2.start();
							t.stop();
							return;
						}
						setText(message.substring(1));
					}
				});
				t.start();
			}
		};
	}

	public void addMessage(InformationMessage message) {
		messageList.add(message);
	}
	
	public void pushMessage(InformationMessage message) {
		messageList.add(0, message);
		setText(message.getMessage());
	}
}
