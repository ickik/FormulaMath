package fr.ickik.formulamath.controler;

/**
 * Listener interface that receive a message to update the component.
 * @author Ickik
 * @version 0.1.000, 22 June 2012
 * @since 0.3.5
 */
public interface InformationMessageListener {

	/**
	 * Message to display in the listen component.
	 * @param message the message.
	 */
	public void displayMessage(String message);
}
