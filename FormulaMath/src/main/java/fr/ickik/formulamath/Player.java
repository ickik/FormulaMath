package fr.ickik.formulamath;

import java.awt.Color;

/**
 * Player object defines configuration for a player (Human or AI).
 * @author Patrick Allgeyer.
 * @version 0.1.000, 30 sept. 2011.
 */
public class Player {

	private static int GENERAL_ID = 1;

	private final int id;
	private final Position position;
	private final Vector movingVector;
	private final String name;
	private final PlayerType type;
	private int playingCounter = 0;
	private final Color[] colorList = new Color[] { Color.RED, Color.BLACK, Color.BLUE, Color.YELLOW };

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
		position = new Position();
		movingVector = new Vector();
	}

	public int getId() {
		return id;
	}

	public Vector getVector() {
		return movingVector;
	}

	public Position getPosition() {
		return position;
	}

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
		return id + " " + name + " (" + type + ") :" + position.toString();
	}
	
	public void incrementGoCounter() {
		playingCounter++;
	}
	
	public int getGoCounter() {
		return playingCounter;
	}
}
