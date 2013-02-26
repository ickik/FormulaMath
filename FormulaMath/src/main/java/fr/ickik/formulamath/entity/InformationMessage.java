package fr.ickik.formulamath.entity;

/**
 * Class representing a message displayed in the information label.
 * It is composed by a type which gives the type of message ({@link MessageType})
 * and the textual message in String.
 * @author Ickik
 * @version 0.1.000, 8 February 2013.
 * @since 0.3.9
 */
public class InformationMessage {

	private final MessageType type;
	private final String message;
	
	/**
	 * Constructor which initializes the type of the message and the
	 * message to display.
	 * @param type the type of message. (see {@link MessageType}).
	 * @param message the message associated to the type.
	 */
	public InformationMessage(MessageType type, String message) {
		this.type = type;
		this.message = message;
	}

	/**
	 * Return the type of this message.
	 * @return the type of the message.
	 */
	public MessageType getType() {
		return type;
	}

	/**
	 * Return the message associated to the InformationMessage.
	 * @return the message.
	 */
	public String getMessage() {
		return message;
	}
}
