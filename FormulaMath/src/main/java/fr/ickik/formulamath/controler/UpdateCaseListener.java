package fr.ickik.formulamath.controler;

import fr.ickik.formulamath.Player;

/**
 * Listener of player's moving. It update the screen depending position
 * and solution offers to the player.
 * @author Ickik.
 * @version 0.1.000, 30 sept. 2011.
 */
public interface UpdateCaseListener {

	/**
	 * Updates player's position on the map.
	 * @param player
	 */
	void updatePlayerCase(Player player);
	
	/**
	 * Updates on screen position possible for the player.
	 * @param player the player on who update the possibilities.
	 */
	void updatePlayerPossibilities(Player player);
	
	/**
	 * Update the screen and position of the player.
	 * @param player
	 */
	void updateEndGamePanel(Player player);
}
