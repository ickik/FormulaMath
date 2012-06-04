package fr.ickik.updater;

/**
 * Interface to implements to inform view that the update progression
 * and that the application will restart.
 * @author Ickik
 * @version 0.1.000, 1 june 2012
 */
public interface UpdaterListener {

	/**
	 * Update progress bar view with the value given in argument and display message
	 * to inform user of the state of the update.
	 * @param value the value of the progress bar.
	 * @param msg the information message to display.
	 */
	void updateValue(int value, String msg);

	/**
	 * Inform that the application will restart.
	 */
	void restart();
}
