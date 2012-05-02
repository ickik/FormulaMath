package fr.ickik.formulamath.model.map;

import java.util.concurrent.Callable;

/**
 * 
 * @author Ickik
 * @version 0.1.000, 19 apr. 2012
 */
public class MapManagerConstructor implements Callable<MapManager> {

	@Override
	public MapManager call() throws Exception {
		MapManager manager = new MapManager();
		manager.init(100);
		manager.constructRoad();
		return manager;
	}

}
