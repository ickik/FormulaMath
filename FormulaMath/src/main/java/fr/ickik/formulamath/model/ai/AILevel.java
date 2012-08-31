package fr.ickik.formulamath.model.ai;


import java.util.Map;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;

/**
 * Interface that defines the generic behavior of computer player.
 * @author Ickik.
 * @version 0.1.001, 31 August 2012
 * @since 0.3.1
 */
public interface AILevel {

	/**
	 * Return the optimal starting position depending the level of the AI.
	 * @return the optimal starting position.
	 */
	Position getStartingPosition();
	
	/**
	 * Return the first optimal moving {@link Vector}.
	 * @param player the current player which must choose starting vector.
	 * @param playerRoadPosition the Map of index of player which defines the Position in the ideal way.
	 * The ideal way is defined in the Road in MapManager.
	 * @return a optimal starting Vector.
	 */
	Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition);
	
	/**
	 * Return the next play depending Player last Vector moving.
	 * @param player
	 * @param playerRoadPosition the Map of player <-> index which defines the position in the ideal way.
	 * The ideal way is defined in the Road in MapManager.
	 * @return
	 */
	Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) throws FormulaMathException;
	
	/**
	 * Return true if the getNextPlay is the last move that pass the finish line.
	 * @return true if the getNextPlay return the last moving {@link Vector}, false otherwise.
	 */
	boolean isLastPlay();
	
	/**
	 * Set the boolean value to false, to replay.
	 */
	void reinitIsLastPlay();
}
