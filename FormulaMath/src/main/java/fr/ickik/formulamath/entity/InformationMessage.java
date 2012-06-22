package fr.ickik.formulamath.entity;

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
