package fr.ickik.formulamath.model;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import fr.ickik.formulamath.model.map.MapManager;

public class MapManagerTest {

	public static void main(String[] args) {
		try {
		final int size = 100;
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < 1000; i++) {
		long t = System.currentTimeMillis();
		MapManager manager = new MapManager(size);
		manager.init();
		manager.constructRoad();
		l.add((System.currentTimeMillis() - t) + "ms   number of direction :" + manager.getRoadDirectionInformationList().size());
		}
		for (String s : l) {
			System.out.println(s);
		}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Test
	public void MapGeneration() {
		final int size = 100;
		MapManager manager = new MapManager(size);
		manager.init();
		manager.constructRoad();
		String mapString = manager.toString();
		String[] map = mapString.split("\n");
		//assertEquals(map.length, size);
	}
}
