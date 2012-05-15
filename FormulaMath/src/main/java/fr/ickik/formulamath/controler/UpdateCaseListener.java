package fr.ickik.formulamath.controler;

import java.util.List;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;

/**
 * Listener of player's moving. It update the screen depending position
 * and solution offers to the player.
 * @author Ickik.
 * @version 0.1.002, 15 mai 2012.
 */
public interface UpdateCaseListener {

	/**
	 * Updates player's position on the map.
	 */
	void updatePlayerCase();
	
	/**
	 * Update the screen to display the end game panel.
	 */
	void updateEndGamePanel();
	
	/**
	 * display all positions of the starting line. The player given in argument
	 * could choose a position to start the game.
	 * @param player the player who is under playing.
	 * @param startingPositionList the list of position available on starting line.
	 * @param mapSize the size of the map.
	 */
	void displayPlayerStartingPossibilities(Player player, List<Position> startingPositionList, int mapSize);
	
	void displayPlayerFirstMove(Player player, int mapSize);
	
	void displayPlayerMovePossibilities(Player currentPlayer, List<Vector> list, int mapSize);
}
