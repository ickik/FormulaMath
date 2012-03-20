package fr.ickik.formulamath.entity;

import java.awt.Color;

import fr.ickik.formulamath.PlayerType;

/**
 * Player object defines configuration for a player (Human or AI).
 * A player is identified by an unique id. Other characteristics are
 * the name, the type of the player ({@link PlayerType}), a {@link Color},
 * a position and the last move.
 * @author Patrick Allgeyer.
 * @version 0.1.000, 30 sept. 2011.
 */
public class Player {

	private static int GENERAL_ID = 1;

	private final int id;
	private final Position position = new Position();
	private final Vector movingVector = new Vector();
	private final String name;
	private final PlayerType type;
	private int playingCounter = 0;
	private static final Color[] colorList = new Color[] { Color.RED, Color.BLACK, Color.BLUE, Color.YELLOW };

	/**
	 * Constructor of the Player, it needs the type of player and the
	 * name associates to this player. The constructor creates an unique
	 * id to identify every player.
	 * @param type the type of the player Human or AI.
	 * @param name the name of the player.
	 */
	public Player(PlayerType type, String name) {
		id = GENERAL_ID++;
		this.type = type;
		this.name = name;
	}

	/**
	 * Return the unique id of the player.
	 * @return the id of the player.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Return the vector of the last move of this player.
	 * @return the last move of the player.
	 */
	public Vector getVector() {
		return movingVector;
	}

	/**
	 * Return the current position in the map of the player. The position
	 * is define in coordinates which the starting point of the map is
	 * the area without blank frame.
	 * @return the current position in the map.
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Return the name of the player.
	 * @return the name of the player.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the type of the player : Human or Computer {@link PlayerType}.
	 * @return the type of the player : Human or Computer (enum).
	 */
	public PlayerType getType() {
		return type;
	}

	/**
	 * Return the color of the player.
	 * @return the color associates to the player.
	 */
	public Color getPlayerColor() {
		return colorList[id - 1];
	}

	@Override
	public String toString() {
		return Integer.toString(id) + " " + name + " (" + type + ") :" + position.toString();
	}
	
	/**
	 * Increment the playing counter. The counter indicates the number
	 * of turn the player needs to finish the road.
	 */
	public void incrementPlayingCounter() {
		playingCounter++;
	}
	
	/**
	 * Return the number of turn the player needs to finish the game.
	 * @return the number of turn the player needs to finish the game.
	 */
	public int getPlayingCounter() {
		return playingCounter;
	}
}
