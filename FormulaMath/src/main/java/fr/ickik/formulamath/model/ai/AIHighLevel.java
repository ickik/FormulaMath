package fr.ickik.formulamath.model.ai;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.MapManager;

public class AIHighLevel implements AILevel {

	private final MapManager mapManager;
	private final Map<Integer, Integer> playerRoadPosition = new HashMap<Integer, Integer>();
	private static final Logger log = LoggerFactory.getLogger(AIMediumLevel.class);

	public AIHighLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

}
