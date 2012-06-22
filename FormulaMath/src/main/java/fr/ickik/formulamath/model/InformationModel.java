package fr.ickik.formulamath.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

import fr.ickik.formulamath.controler.InformationMessageListener;
import fr.ickik.formulamath.entity.InformationMessage;

/**
 * 
 * @author Ickik
 * @version 0.1.000, 22 June 2012
 * @since 0.3.5
 */
public final class InformationModel {

	private final Timer timer;
	private final int delay = 4000;
	private final LinkedList<InformationMessage> messageDeque = new LinkedList<InformationMessage>();
	private final List<InformationMessageListener> informationMessageListenerList = new ArrayList<InformationMessageListener>();
	private String informationMessage;
	
	public InformationModel() {
		timer = new Timer(delay, getTimerActionListener());
	}
	
	public void addInformationMessageListener(InformationMessageListener listener) {
		if (!timer.isRunning()) {
			timer.start();
		}
		informationMessageListenerList.add(listener);
	}
	
	private ActionListener getTimerActionListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final Timer t2 = new Timer(20, null);
				t2.addActionListener(new ActionListener() {
					int index=1;
					@Override
					public void actionPerformed(ActionEvent e) {
						String msg = messageDeque.removeFirst().getMessage();
						fireMessageListener(msg.substring(0, index++));
						t2.stop();
						informationMessage = msg;
					}
				});
				final Timer t = new Timer(20, null);
				t.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						String message = informationMessage;
						int len = message.length();
						if (len == 1) {
							fireMessageListener("");
							t2.start();
							t.stop();
							return;
						}
						informationMessage = message.substring(1);
						fireMessageListener(informationMessage);
					}
				});
				t.start();
			}
		};
	}
	
	private void fireMessageListener(String message) {
		for (InformationMessageListener listener : informationMessageListenerList) {
			listener.displayMessage(message);
		}
	}

	public void addMessage(InformationMessage message) {
		messageDeque.add(message);
	}
	
	public void pushMessage(InformationMessage message) {
		messageDeque.addFirst(message);
		fireMessageListener(message.getMessage());
	}
}
