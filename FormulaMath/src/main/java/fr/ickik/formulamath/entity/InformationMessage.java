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
	
	public InformationMessage(MessageType type, String message) {
		this.type = type;
		this.message = message;
	}

	public MessageType getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
}
