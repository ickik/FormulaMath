package fr.ickik.formulamath.model.ai;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;

/**
 * Computer Easy Level. This class choose a simple vector to move. <br>What is a simple Vector?
 * A simple Vector is a vector with a length of 1 case in all direction. It is no dynamic intelligence
 * to move.
 * @author Ickik
 * @version 0.1.002, 3rd Septembre 2012
 * @since 0.3.9
 */
public final class AIEasyLevel implements AILevel {

	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AIEasyLevel.class);
	private boolean isLastPlay = false;
	
	public AIEasyLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) throws FormulaMathException {
		if (player == null) {
			throw new FormulaMathException("The player parameter should not be null");
		}
		int roadPosition = 0;
		if (playerRoadPosition.get(player.getId()) != null) {
			roadPosition = playerRoadPosition.get(player.getId());
		}
		RoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(roadPosition);
		int len = r.getLengthToEnd(player.getPosition()) - 1;
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		if (len == 1 || len == -1) {
			RoadDirectionInformation nextRoadDirection = mapManager.getDetailledRoadDirectionInformationList().get(roadPosition + 1);
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
			}
			if (vector != null && len < Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY())) {
				isLastPlay = true;
			}
		}
		if (player != null && player.getPosition() != null && vector != null) {
			CaseModel c = mapManager.getCase(player.getPosition().getY() + vector.getY(), player.getPosition().getX()  + vector.getX());
			if (c != null && c.isOccuped()) {
				vector = new Vector(0, 0);
			}
		}
		return vector;
	}

	@Override
	public boolean isLastPlay() {
		return isLastPlay;
	}

	@Override
	public Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition) {
		int roadPosition = playerRoadPosition.get(player.getId());
		RoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(roadPosition);
		Vector vector = null;
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
		}
		return vector;
	}

	@Override
	public Position getStartingPosition() {
		List<Position> list = mapManager.getStartingPositionList();
		Position position = list.remove(0);
		return position;
	}

	@Override
	public void reinitIsLastPlay() {
		this.isLastPlay = false;
	}

}
