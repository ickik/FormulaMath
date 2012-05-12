package fr.ickik.formulamath.controler;

import java.util.List;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;

/**
 * Listener of player's moving. It update the screen depending position
 * and solution offers to the player.
 * @author Ickik.
 * @version 0.1.001, 11 mai 2012.
 */
public interface UpdateCaseListener {

	/**
	 * Updates player's position on the map.
	 */
	void updatePlayerCase();
	
	/**
	 * Updates on screen position possible for the player.
	 * @param player the player on who update the possibilities.
	 */
	void updatePlayerPossibilities(Player player);
	
	/**
	 * Update the screen and position of the player.
	 */
	void updateEndGamePanel();
	
	void displayPlayerStartingPossibilities(Player player, List<Position> startingPositionList, int mapSize);
	void displayPlayerFirstMove(Player player, int mapSize);
	
	void displayPlayerMovePossibilities(Player currentPlayer, List<Vector> list, int mapSize);
}
