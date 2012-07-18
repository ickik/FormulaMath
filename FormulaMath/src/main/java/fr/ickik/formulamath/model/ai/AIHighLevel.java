package fr.ickik.formulamath.model.ai;

import java.util.Map;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.MapManager;

public class AIHighLevel implements AILevel {

	private final MapManager mapManager;
	//private static final Logger log = LoggerFactory.getLogger(AIMediumLevel.class);
	private boolean isLastPlay = false;
	
	public AIHighLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player, Map<Integer, Integer> playerRoadPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLastPlay() {
		return isLastPlay;
	}
	
	@Override
	public void reinitIsLastPlay() {
		this.isLastPlay = false;
	}

	@Override
	public Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position getStartingPosition() {
		mapManager.getStartingPositionList();
		return null;
	}

}
