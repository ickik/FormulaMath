package fr.ickik.formulamath.model;

import org.testng.annotations.Test;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

public class PlayerManagerTest {

	@Test
	public void addPlayerTest() {
		MapManager mapManager = new MapManager();
		PlayerManager manager = new PlayerManager(mapManager);
		assertEquals(manager.getPlayerList().size(), 0);
		manager.addPlayer(null);
		assertEquals(manager.getPlayerList().size(), 0);
		Player p = new Player(PlayerType.HUMAN, "toto");
		manager.addPlayer(p);
		assertEquals(manager.getPlayerList().size(), 1);
		assertEquals(manager.getPlayerList().get(0), p);
		assertNotEquals(manager.getPlayerList().get(0), new Player(PlayerType.HUMAN, "tata"));
	}
}
