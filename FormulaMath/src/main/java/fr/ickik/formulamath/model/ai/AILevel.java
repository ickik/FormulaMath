package fr.ickik.formulamath.model.ai;


import java.util.Map;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;

/**
 * Interface that defines the generic behavior of computer player.
 * @author Ickik.
 * @version 0.1.000, 31 mai 2012
 * @since 0.3.1
 */
public interface AILevel {

	//Position getStartingPosition(Player player);
	//Vector getFirstMove(Player player);
	Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition);
}
