package fr.ickik.formulamath.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.ickik.formulamath.controler.InformationMessageListener;
import fr.ickik.formulamath.entity.InformationMessage;

/**
 * Model of the panel that displays information periodically.
 * @author Ickik
 * @version 0.1.002, 28 June 2012
 * @since 0.3.5
 */
public final class InformationModel {

	private Timer timer;
	private final int delay = 4000;
	private final LinkedList<InformationMessage> messageDeque = new LinkedList<InformationMessage>();
	private final List<InformationMessageListener> informationMessageListenerList = new ArrayList<InformationMessageListener>();
	private final int sleepTime = 30;
	
	public InformationModel() {}
	
	public void addInformationMessageListener(InformationMessageListener listener) {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(getTimerTask(), 1000, delay);
		}
		informationMessageListenerList.add(listener);
	}
	
	private TimerTask getTimerTask() {
		return new TimerTask() {
			
			private String informationMessage = " ";
			@Override
			public void run() {
				try {
					if (informationMessage.length() > 1) {
						shiftingOldMessage(informationMessage);
					}
					if (messageDeque.isEmpty()) {
						informationMessage = " ";
						fireMessageListener(informationMessage);
						return ;
					}
					informationMessage = displayNewMessage();
					fireMessageListener(informationMessage);
				} catch (InterruptedException e) {}
			}
		};
	}
	
	private void shiftingOldMessage(String message) throws InterruptedException {
		String msg = message;
		boolean isShift = false;
		while (!isShift) {
			if (msg.length() == 1) {
				fireMessageListener(" ");
				isShift = true;
			}
			msg = msg.substring(1);
			fireMessageListener(msg);
			Thread.sleep(sleepTime);
		}
	}
	
	private String displayNewMessage() throws InterruptedException {
		String msg = messageDeque.removeFirst().getMessage();
		boolean end = false;
		int index = 1;
		while (!end) {
			fireMessageListener(msg.substring(0, index++));
			Thread.sleep(sleepTime);
			if (msg.length() == index) {
				end = true;
			}
		}
		return msg;
	}
	
	public void stop() {
		timer.purge();
		timer.cancel();
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