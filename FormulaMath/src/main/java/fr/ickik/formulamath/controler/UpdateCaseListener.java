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
	 * @param x
	 * @param y
	 * @param p
	 */
	void updatePlayerCase(int x, int y, Player p);
	
	/**
	 * Updates on screen position possible for the player.
	 * @param p the player on who update the possibilities.
	 */
	void updatePlayerPossibilities(Player p);
}
