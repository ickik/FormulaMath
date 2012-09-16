package fr.ickik.formulamath.model;

import org.testng.annotations.Test;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.player.PlayerManager;
import fr.ickik.formulamath.model.player.PlayerType;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertFalse;

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
	
	@Test
	public void addPlayerTest2() {
		MapManager mapManager = new MapManager();
		PlayerManager manager = new PlayerManager(mapManager);
		assertEquals(manager.getPlayerList().size(), 0);
		manager.addPlayer(null);
		assertEquals(manager.getPlayerList().size(), 0);
		Player p = new Player(PlayerType.HUMAN, "toto");
		Player p2 = new Player(PlayerType.HUMAN, "tata");
		manager.addPlayer(p);
		manager.addPlayer(p2);
		assertEquals(manager.getPlayerList().size(), 2);
		assertEquals(manager.getPlayerList().get(0), p);
		assertEquals(manager.getPlayerList().get(1), p2);
		assertFalse(manager.getPlayerList().contains(new Player(PlayerType.HUMAN, "tata")));
	}
}
