package fr.ickik.formulamath.entity;

/**
 * Defines all types which a message can have. The type of message defines his importance.
 * @author Ickik
 * @version 0.1.000, 13 June 2012.
 * @since 0.3.9.
 */
public enum MessageType {

	/**
	 * Information message.
	 */
	INFORMATION,
	
	/**
	 * Information about the player.
	 */
	PLAYER,
	
	/**
	 * Information relative to statistics about game and player.
	 */
	STATS;
}
