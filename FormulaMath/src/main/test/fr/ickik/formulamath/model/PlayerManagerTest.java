package fr.ickik.formulamath.model;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;

public class PlayerManagerTest {

	@Test
	public void addPlayerTest() {
		PlayerManager manager = PlayerManager.getInstance();
		assertEquals(manager.getPlayerList().size(), 0);
		manager.addPlayer(null);
		assertEquals(manager.getPlayerList().size(), 0);
		Player p = new Player(PlayerType.HUMAN, "toto");
		manager.addPlayer(p);
		assertEquals(manager.getPlayerList().size(), 1);
		assertEquals(PlayerManager.getInstance().getPlayerList().get(0), p);
		assertEquals(manager.getNumberOfHumanPlayer(), 1);
	}
}
