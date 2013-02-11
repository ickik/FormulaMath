package fr.ickik.formulamath.model.ai;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.entity.DetailledRoadDirectionInformation;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;

/**
 * Class implements a high level computer intelligence.
 * @author Ickik
 * @version 0.1.003, 8 February 2013.
 * @since 0.3.9
 */
public final class AIHighLevel extends AbstractAILevel {

	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AIHighLevel.class);
	
	public AIHighLevel(MapManager mapManager) {
		super(mapManager);
		this.mapManager = mapManager;
	}
	
	private int getNextLength(int length, int position, boolean isLastDirection) {
		log.trace("getNextLength({}, {}, {})", new Object[]{length, position, isLastDirection});
		if (isLastDirection) {
			return 1;
		}
		return getNextPlay(length, position);
	}
	
	@Override
	public Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) throws FormulaMathException {
		log.debug("GetNextPlay begin pour {}, suivant la map {}", player, playerRoadPosition);
		if (player == null) {
			log.error("The player argument should not be null");
			throw new FormulaMathException("The player parameter should not be null");
		}
		int roadPosition = 0;
		log.debug("playerRoadPosition for player id {} = {}", player.getId(), playerRoadPosition.get(player.getId()));
		if (playerRoadPosition.get(player.getId()) != null) {
			roadPosition = playerRoadPosition.get(player.getId());
		}
		log.debug("roadPoistion found : {}", roadPosition);
		DetailledRoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(roadPosition);
		log.trace("Detailled road found : {}", r);
		int len = r.getLengthToEnd(player.getPosition()) - 1;
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		List<Vector> solutionList = getVectorsPossibilities(player);
		log.trace("Solution offers to player : {}", solutionList);
		if (solutionList.isEmpty()) {
			log.info("No solutions, return 'null'");
			return null;
		}
		boolean isLastDirection = mapManager.getDetailledRoadDirectionInformationList().size() == roadPosition + 1;
		log.debug("is it the last road ? {}", isLastDirection);
		if (len > 3 || isLastDirection) {
			log.trace("Len = {}, isLastDirection={}", len, isLastDirection);
			log.trace("orientation {}", r.getOrientation());
			switch (r.getOrientation()) {
			case NORTH:
				int d = getNextLength(len, player.getVector().getY(), isLastDirection);
				log.debug("Next play : {}", d);
				vector = new Vector(0, player.getVector().getY() + d);
				break;
			case SOUTH:
				d = getNextLength(len, player.getVector().getY(), isLastDirection);
				log.debug("Next play : {}", d);
				vector = new Vector(0, player.getVector().getY() - d);
				break;
			case WEST:
				d = getNextLength(len, player.getVector().getX(), isLastDirection);
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() - d, 0);
				break;
			case EAST:
				d = getNextLength(len, player.getVector().getX(), isLastDirection);
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() + d, 0);
				break;
			}
			log.trace("Vector found {}", vector);
			if (solutionList.contains(vector)) {
				log.debug("SolutionList contains the vector {}", vector);
				return vector;
			}
			log.trace("No solution found, search an approx solution");
			Vector tmpVector = null;
			for (Vector v : solutionList) {
				switch (r.getOrientation()) {
				case NORTH:
					if ((v.getY() == vector.getY() && player.getPosition().getX() == r.getEnd().getX() && vector.getX() == 0) || (v.getY() == vector.getY() - 1 && vector.getX() == 0)) {
						tmpVector = v;
					}
					break;
				case SOUTH:
					if ((v.getY() == vector.getY() && player.getPosition().getX() == r.getEnd().getX() && vector.getX() == 0) || (v.getY() == vector.getY() - 1 && vector.getX() == 0)) {
						tmpVector = v;
					}
					break;
				case WEST:
					if ((v.getX() == vector.getX() && player.getPosition().getY() == r.getEnd().getY() && vector.getY() == 0) || (v.getX() == vector.getX() - 1 && vector.getY() == 0)) {
						tmpVector = v;
					}
					break;
				case EAST:
					if ((v.getX() == vector.getX() && player.getPosition().getY() == r.getEnd().getY() && vector.getY() == 0) || (v.getX() == vector.getX() - 1 && vector.getY() == 0)) {
						tmpVector = v;
					}
					break;
				}
			}
			log.debug("Aproximative solution found {}", tmpVector);
			if (tmpVector != null) {
				return tmpVector;
			}
			int min = Integer.MAX_VALUE;
			log.trace("No solution found, search the easiest solution");
			for (Vector v : solutionList) {
				switch (r.getOrientation()) {
				case NORTH:
					if (v.getY() == vector.getY() - 1 && v.getX() < min) {
						tmpVector = v;
						min = v.getX();
					}
					break;
				case SOUTH:
					if (v.getY() >= vector.getY() + 1) {
						tmpVector = v;
					}
					break;
				case WEST:
					if (v.getX() <= vector.getX() + 1) {
						tmpVector = v;
					}
					break;
				case EAST:
					if (v.getX() >= vector.getX() - 1) {
						tmpVector = v;
					}
					break;
				}
			}
			log.debug("Easiest solution {}", tmpVector);
			return tmpVector;
		}
		log.trace("Player in curve");
		Position endPosition = r.getEnd();
		log.trace("End position of the curve : {}", endPosition);
		for (Vector v : solutionList) {
			Position tmp = new Position(player.getPosition().getX() + v.getX(), player.getPosition().getY() - v.getY());
			if (endPosition.equals(tmp) && !mapManager.getCase(tmp.getY(), tmp.getX()).isOccuped()) {
				playerRoadPosition.put(player.getId(), roadPosition + 1);
				log.debug("Solution found to equals the end of the curve {}", v);
				return v;
			}
		}

		int minValue = Integer.MAX_VALUE;
		log.trace("No solution found, search an approx. solution");
		for (Vector v : solutionList) {
			Position tmp = new Position(player.getPosition().getX() + v.getX(), player.getPosition().getY() - v.getY());
			int val = getNextVector(v, tmp, r.getInitialOrientation(), r.getOrientation(), endPosition, 1);
			if (val < minValue) {
				minValue = val;
				vector = v;
			}
		}
		
		playerRoadPosition.put(player.getId(), roadPosition + 1);
		log.debug("Returned approx. vector {}", vector);
		return vector;
	}
	
	private int getNextVector(Vector vector, Position position, Orientation currentOrientation, Orientation nextOrientation, Position endPosition, int step) {
		List<Vector> list = getVectorsSolution(position, vector);
		
		for (Vector vect : list) {
			switch(currentOrientation) {
			case NORTH:
				switch(nextOrientation) {
				case EAST:
					if (vect.getX() < 0 || vect.getY() < 0) {
						continue;
					}
					break;
				case WEST:
					if (vect.getX() > 0 || vect.getY() < 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			case SOUTH:
				switch(nextOrientation) {
				case EAST:
					if (vect.getX() < 0 || vect.getY() > 0) {
						continue;
					}
					break;
				case WEST:
					if (vect.getX() < 0 || vect.getY() > 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			case EAST:
				switch(nextOrientation) {
				case NORTH:
					if (vect.getY() < 0 || vect.getX() < 0) {
						continue;
					}
					break;
				case SOUTH:
					if (vect.getY() > 0 || vect.getX() < 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			case WEST:
				switch(nextOrientation) {
				case NORTH:
					if (vect.getY() < 0 || vect.getX() > 0) {
						continue;
					}
					break;
				case SOUTH:
					if (vect.getY() > 0 || vect.getX() > 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			}
			
			Position tmpPos = new Position(position.getX() + vect.getX(), position.getY() - vect.getY());
			if (endPosition.equals(tmpPos)) {
				return step;
			}
			return getNextVector(vect, tmpPos, currentOrientation, nextOrientation, endPosition, step+1);
		}
		return Integer.MAX_VALUE;
	}
}
