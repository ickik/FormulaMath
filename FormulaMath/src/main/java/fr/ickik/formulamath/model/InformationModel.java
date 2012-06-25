package fr.ickik.formulamath.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.ickik.formulamath.controler.InformationMessageListener;
import fr.ickik.formulamath.entity.InformationMessage;

/**
 * 
 * @author Ickik
 * @version 0.1.001, 25 June 2012
 * @since 0.3.5
 */
public final class InformationModel {

	private Timer timer;
	private final int delay = 4000;
	private final LinkedList<InformationMessage> messageDeque = new LinkedList<InformationMessage>();
	private final List<InformationMessageListener> informationMessageListenerList = new ArrayList<InformationMessageListener>();
	private String informationMessage;
	private TimerTask task;
	
	public InformationModel() {
		timer = new Timer();
		
	}
	
	public void addInformationMessageListener(InformationMessageListener listener) {
		if (task == null) {
			task = getTimerTask();
			timer.schedule(task, 1000, delay);
		}
		informationMessageListenerList.add(listener);
	}
	
	private TimerTask getTimerTask() {
		return new TimerTask() {
			
			@Override
			public void run() {
				final Timer t2 = new Timer();
				t2.schedule(new TimerTask() {
					int index=1;
					@Override
					public void run() {
						if (messageDeque.isEmpty()) {
							t2.purge();
							return;
						}
						String msg = messageDeque.removeFirst().getMessage();
						fireMessageListener(msg.substring(0, index++));
						
						informationMessage = msg;
					}
				}, 0, 20);
				final Timer t = new Timer();
				t.schedule(new TimerTask() {
					@Override
					public void run() {
						if (informationMessage != null) {
							String message = informationMessage;
							int len = message.length();
							if (len == 1) {
								fireMessageListener("");
								t2.purge();
								t.purge();
								return;
							}
							informationMessage = message.substring(1);
							fireMessageListener(informationMessage);
						}
					}
				}, 0, 20);
			}
		};
	}
	
	/*private ActionListener getTimerActionListener() {
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
						if (informationMessage != null) {
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
					}
				});
				t.start();
			}
		};
	}*/
	
	public void stop() {
		timer.purge();
		timer.cancel();
		/*if (timer.isRunning()) {
			timer.stop();
			for (ActionListener al : timer.getActionListeners()) {
				timer.removeActionListener(al);
			}
			timer = null;
		}*/
		timer = null;
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
