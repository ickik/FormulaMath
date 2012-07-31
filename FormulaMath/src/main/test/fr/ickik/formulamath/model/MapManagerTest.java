package fr.ickik.formulamath.model;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;

public class MapManagerTest {

	private final int size = 50;

	@Test
	public void mapGeneration() {
		MapManager manager = new MapManager();
		manager.init(size);
		assertEquals(manager.getMapSize(), size);
		assertEquals(manager.getRoadDirectionInformationList().size(), 0);
		assertEquals(manager.getFinishingLinePositionList().size(), 0);
		assertEquals(manager.getStartingPositionList().size(), 0);
		for (int i = 0; i< size ; i++) {
			for (int j = 0; j< size ; j++) {
				assertNotNull(manager.getCase(i, j).getField());
				assertNotEquals(manager.getCase(i, j).getField(), Field.STARTING_LINE);
				assertNotEquals(manager.getCase(i, j).getField(), Field.ROAD);
				assertNotEquals(manager.getCase(i, j).getField(), Field.FINISHING_LINE);
			}
		}
	}
	
	@Test
	public void mapGenerationConstruction() {
		MapManager manager = new MapManager();
		manager.init(size);
		assertEquals(manager.getMapSize(), size);
		manager.constructRoad();
		assertNotEquals(manager.getRoadDirectionInformationList().size(), 0);
		assertEquals(manager.getFinishingLinePositionList().size(), 2);
		assertEquals(manager.getStartingPositionList().size(), MapManager.ROAD_SIZE);
	}
	
	@Test
	public void mapReinitialisation() {
		MapManager manager = new MapManager();
		manager.init(size);
		manager.constructRoad();
		int roadSize = manager.getRoadDirectionInformationList().size();
		manager.reinitialization();
		assertEquals(manager.getRoadDirectionInformationList().size(), roadSize);
		assertEquals(manager.getFinishingLinePositionList().size(), 2);
		assertEquals(manager.getStartingPositionList().size(), MapManager.ROAD_SIZE);
	}
	
	@Test
	public void mapFullReinitialisation() {
		MapManager manager = new MapManager();
		manager.init(size);
		manager.constructRoad();
		manager.fullReinitialization();
		assertEquals(manager.getRoadDirectionInformationList().size(), 0);
		assertEquals(manager.getFinishingLinePositionList().size(), 0);
		assertEquals(manager.getStartingPositionList().size(), 0);
	}
}
