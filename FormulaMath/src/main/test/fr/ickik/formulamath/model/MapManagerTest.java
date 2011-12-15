package fr.ickik.formulamath.model;

import org.testng.annotations.Test;
import static  org.testng.Assert.assertEquals;

public class MapManagerTest {

	@Test
	public void MapGeneration() {
		final int size = 100;
		MapManager manager = new MapManager(size);
		String mapString = manager.toString();
		String[] map = mapString.split("\n");
		assertEquals(map.length, size);
	}
}
