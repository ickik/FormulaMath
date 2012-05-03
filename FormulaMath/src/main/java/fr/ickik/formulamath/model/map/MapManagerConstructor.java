package fr.ickik.formulamath.model.map;

import java.util.concurrent.Callable;

/**
 * 
 * @author Ickik
 * @version 0.1.001, 3 mai 2012
 */
public class MapManagerConstructor implements Callable<MapManager> {

	private final MapManager mapManager;
	private final int size;
	
	public MapManagerConstructor(MapManager mapManager, int size) {
		this.mapManager = mapManager;
		this.size = size;
	}

	@Override
	public MapManager call() throws Exception {
		mapManager.init(size);
		mapManager.constructRoad();
		return mapManager;
	}

}
