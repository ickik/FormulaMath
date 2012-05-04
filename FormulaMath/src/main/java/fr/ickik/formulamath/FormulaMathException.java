package fr.ickik.formulamath;

/**
 * FormulaMathException extends Exception and redefine their behavior.
 * This class was created to harmonize exceptions in the application.
 * @author Ickik
 * @version 0.1.000, 12 apr. 2012
 */
public class FormulaMathException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor of the Exception.
	 */
	public FormulaMathException() {
		super();
	}

	/**
	 * Constructor of the exception with an appropriate message.
	 * @param message the message to add to the exception.
	 */
	public FormulaMathException(String message) {
		super(message);
	}
}
