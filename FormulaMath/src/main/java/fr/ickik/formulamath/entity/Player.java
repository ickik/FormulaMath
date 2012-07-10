package fr.ickik.formulamath.entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.ickik.formulamath.model.player.PlayerType;

/**
 * Player object defines configuration for a player (Human or AI).
 * A player is identified by an unique id. Other characteristics are
 * the name, the type of the player ({@link PlayerType}), a {@link Color},
 * a position and the last move.
 * @author Patrick Allgeyer.
 * @version 0.1.004, 10 July 2012.
 */
public class Player {

	private static int GENERAL_ID = 1;

	private final int id;
	private final Position position = new Position();
	private final Vector movingVector = new Vector();
	private final String name;
	private final PlayerType type;
	private int playingCounter = 0;
	private final List<Vector> movingList = new ArrayList<Vector>();
	

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

	@Override
	public String toString() {
		return "id:" + Integer.toString(id) + " name:" + name + " (" + type + ") :" + position.toString();
	}
	
	/**
	 * Increment the playing counter and stores the last move in a list.
	 * The counter indicates the number of turn the player needs to finish the road.
	 */
	public void incrementPlayingCounter() {
		movingList.add(new Vector(movingVector.getX(), movingVector.getY()));
		playingCounter++;
	}
	
	/**
	 * Return the number of turn the player needs to finish the game.
	 * @return the number of turn the player needs to finish the game.
	 */
	public int getPlayingCounter() {
		return playingCounter;
	}

	/**
	 * Return the list which contains all moves.
	 * @return the list which contains all moves since the start of the game.
	 */
	public List<Vector> getMovingList() {
		return movingList;
	}
	
	/**
	 * Reinitializes the player statistics variable.
	 */
	public void reinitializePlayer() {
		movingList.clear();
		playingCounter = 0;
	}
	
//	public String getOrientation() {
//		StringBuilder orientation = new StringBuilder();
//		if (movingVector.getY() > 0) {
//			orientation.append(Orientation.NORTH);
//		} else if (movingVector.getY() < 0) {
//			orientation.append(Orientation.SOUTH);
//		}
//		if (orientation.length() > 0) {
//			orientation.append("_");
//		}
//		if (movingVector.getX() > 0) {
//			orientation.append(Orientation.WEST);
//		} else if (movingVector.getX() < 0) {
//			orientation.append(Orientation.EAST);
//		}
//		return orientation.toString();
//	}
}
