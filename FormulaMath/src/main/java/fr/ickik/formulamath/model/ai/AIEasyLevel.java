package fr.ickik.formulamath.model.ai;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;

public class AIEasyLevel implements AILevel {

	private final MapManager mapManager;
	private final Map<Integer, Integer> playerRoadPosition = new HashMap<Integer, Integer>();
	private static final Logger log = LoggerFactory.getLogger(AIEasyLevel.class);

	public AIEasyLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player) {
		int roadPosition = playerRoadPosition.get(player.getId());
		RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
		int len = r.getLengthToEnd(player.getPosition()) - 1;
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		if (len == 1 || len == -1) {
			RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
			switch (r.getOrientation()) {
			case NORTH:
				if (nextRoadDirection.getOrientation() == Orientation.EAST) {
					vector = new Vector(1, 1);
				} else {
					vector = new Vector(-1, 1);
				}
				break;
			case SOUTH:
				if (nextRoadDirection.getOrientation() == Orientation.EAST) {
					vector = new Vector(1, -1);
				} else {
					vector = new Vector(-1, -1);
				}
				break;
			case WEST:
				if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
					vector = new Vector(-1, 1);
				} else {
					vector = new Vector(-1, -1);
				}
				break;
			case EAST:
				if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
					vector = new Vector(1, 1);
				} else {
					vector = new Vector(1, -1);
				}
				break;
			}
		} else {
			switch(r.getOrientation()) {
			case NORTH:
				vector = new Vector(0, -1);
				break;
			case SOUTH:
				vector = new Vector(0, 1);
				break;
			case EAST:
				vector = new Vector(1, 0);
				break;
			case WEST:
				vector = new Vector(-1, 0);
				break;
			};
		}
		return vector;
	}

}
