package fr.ickik.formulamath.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;

/**
 * Class implements a high level computer intelligence.
 * @author Ickik
 * @version 0.1.000, 19 July 2012
 * @since 0.3.9
 */
public class AIHighLevel implements AILevel {

	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AIHighLevel.class);
	private boolean isLastPlay = false;

	public AIHighLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) {
		int roadPosition = playerRoadPosition.get(player.getId());
		RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
		int len = r.getLengthToEnd(player.getPosition()) - 1;
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		List<Vector> solutionList = getVectorsPossibilities(player);
		if (solutionList.isEmpty()) {
			return null;
		}
		if ((Math.abs(len) == 1 || len == 0) && (player.getVector().getX() == 1 ||  player.getVector().getX() == -1 || player.getVector().getY() == 1 || player.getVector().getY() == -1)) {
			RoadDirectionInformation nextRoadDirection;
			if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
			} else {
				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
			}
			playerRoadPosition.put(player.getId(), roadPosition + 1);
			log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
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
			if (solutionList.indexOf(vector) != -1) {
				vector = player.getVector();
			}
			if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
			} else {
				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
			}
			log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
		} else {
			boolean isVerticalAxis = false;
			if (r.getOrientation() == Orientation.NORTH || r.getOrientation() == Orientation.SOUTH) {
				isVerticalAxis = true;
			}
			int minStep = Integer.MAX_VALUE;
			for (Vector v : solutionList) {
				CaseModel model = mapManager.getCase(player.getPosition().getY() - v.getY(), player.getPosition().getX()  + v.getX());
				if (model != null && model.isOccuped()) {
					continue;
				}
				int result = getVectorResult(v, player.getPosition().clone(), len, 1, isVerticalAxis);
				if (minStep > result) {
					minStep = result;
					vector = v;
				}
			}
		}
		
		return vector;
	}
	
	private int getVectorResult(Vector vector, Position position, int distance, int step, boolean isVerticalAxis) {
		if (distance == 0 || distance == 1) {
			return step;
		}
		if (distance < 0) {
			return Integer.MAX_VALUE;
		}
		position.setX(position.getX() + vector.getX());
		position.setY(position.getY() - vector.getY());
		List<Vector> list = getVectorsPossibilities(position, vector);
		int movement = isVerticalAxis ? vector.getX() : vector.getY();
		int len = distance + movement;
		int nbStepMin = Integer.MAX_VALUE;
		for (Vector v : list) {
			int value = getVectorResult(v, position.clone(), len, step + 1, isVerticalAxis);
			if (value < nbStepMin) {
				nbStepMin = value;
			}
		}
		return nbStepMin;
	}


	private List<Vector> getVectorsPossibilities(Player player) {
		log.trace("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);
		log.trace("Player {} , init vector {}", player.toString(), player.getVector().toString());
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(player.getVector());
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX() - 1, player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(new Vector(player.getVector().getX() - 1, player.getVector().getY()));
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX() + 1, player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(new Vector(player.getVector().getX() + 1, player.getVector().getY()));
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY() + 1, player)) {
			list.add(new Vector(player.getVector().getX(), player.getVector().getY() - 1));
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY() - 1, player)) {
			list.add(new Vector(player.getVector().getX(), player.getVector().getY() + 1));
		}
		log.debug("number of vectors possible : {}", list.size());
		for(Vector v : list) {
			log.trace(v.toString());
		}
		log.trace("getVectorsPossibilities exiting");
		return list;
	}
	
	private List<Vector> getVectorsPossibilities(Position position, Vector vector) {
		log.trace("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);
		log.trace("Player {} , init vector {}", position.toString(),vector.toString());
		if (isMovingAvailable(position.getX() + vector.getX(), position.getY() - vector.getY())) {
			list.add(new Vector(vector.getX(), vector.getY()));
		}
		
		if (isMovingAvailable(position.getX() + vector.getX() - 1, position.getY() - vector.getY())) {
			list.add(new Vector(vector.getX() - 1, vector.getY()));
		}
		
		if (isMovingAvailable(position.getX() + vector.getX() + 1, position.getY() - vector.getY())) {
			list.add(new Vector(vector.getX() + 1, vector.getY()));
		}
		
		if (isMovingAvailable(position.getX() + vector.getX(), position.getY() - vector.getY() + 1)) {
			list.add(new Vector(vector.getX(), vector.getY() - 1));
		}
		
		if (isMovingAvailable(position.getX() + vector.getX(), position.getY() - vector.getY() - 1)) {
			list.add(new Vector(vector.getX(), vector.getY() + 1));
		}
		log.debug("number of vectors possible : {}", list.size());
		for(Vector v : list) {
			log.trace(v.toString());
		}
		log.trace("getVectorsPossibilities exiting");
		return list;
	}
	
	private boolean isMovingAvailable(int xMove, int yMove) {
		CaseModel model = mapManager.getCase(yMove, xMove);
		if (model != null && model.getField() == Field.GRASS) {
			return false;
		}
		return true;
	}
	
	private boolean isMovingAvailable(int xMove, int yMove, Player player) {
		log.trace("isMovingAvailable: player id = {}, x={}, y={}", new Object[] {player.getId(), xMove, yMove});
		CaseModel model = mapManager.getCase(yMove, xMove);
		if (model != null && model.getField() != Field.GRASS 
				&& (!model.isOccuped() || model.getIdPlayer() == player.getId())) {
			log.trace("Moving is available");
			return true;
		}
		log.trace("model==null => {}", model==null);
		if (model != null) {
			log.trace("Model field: {}", model.getField());
		}
		return model == null;
	}

	/**
	 * Return the number of step to run the distance depending the speed.
	 * This method is recursively called with a variation of speed from 1, 0 or -1.
	 * @param distance the distance to run
	 * @param vitesse the current speed
	 * @param step the current number of step
	 * @return the number of step to run the distance
	 */
	private int getNbStep(int distance, int vitesse, int step) {
		if ((distance == 0 || distance == 1) && vitesse == 1) {
			return step;
		}
		if (distance < 0 || vitesse <= 0) {
			return Integer.MAX_VALUE;
		}
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step + 1);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step + 1);
		int nbMore = getNbStep(distance - vitesse, vitesse + 1, step + 1);
		return Math.min(nbMore, Math.min(nbLess, nbEqual));
	}
	
	private int getFirstMove(int distance) {
		log.debug("getFirstMove : distance = {}", Integer.toString(distance));
		Map<Integer,Integer> distanceMap = new HashMap<Integer,Integer>();
		if (distance == 1) {
			return 1;
		}
		if (distance > 12) {
			return getHighDistance(distance);
		}
		int halfDistance = distance / 2;
		for (int i = 1; i <= halfDistance; i++) {
			distanceMap.put(getNbStepFirstMove(distance, i, 0), i);
		}
		List<Integer> list = new ArrayList<Integer>(distanceMap.keySet());
		log.debug("Size of the list of the distance found : {}", Integer.toString(list.size()));
		Collections.sort(list);
		return distanceMap.get(list.get(0));
	}
	
	private int getNbStepFirstMove(int distance, int vitesse, int step) {
		if (distance == 0 && vitesse == 1) {
			return step;
		}
		if (distance < 0 || vitesse <= 0) {
			return Integer.MAX_VALUE;
		}
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step +1);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step + 1);
		return Math.min(nbLess, nbEqual);
	}

	public boolean isLastPlay() {
		return isLastPlay;
	}
	
	@Override
	public void reinitIsLastPlay() {
		this.isLastPlay = false;
	}

	@Override
	public Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition) {
		int len = mapManager.getRoadDirectionInformationList().get(0).getLength() - 1;
		log.trace("length of the road : {}, orientation:{}", len, mapManager.getRoadDirectionInformationList().get(0).toString());
		int val = getFirstMove(len);
		log.trace("length of the first move found : {}", val);
		Vector vector = null;
		if (val != 1) {
			switch (mapManager.getRoadDirectionInformationList().get(0).getOrientation()) {
			case NORTH:
				vector = new Vector(0, val);
				break;
			case WEST:
				vector = new Vector(-val, 0);
				break;
			case SOUTH:
				vector = new Vector(0, -val);
				break;
			case EAST:
				vector = new Vector(val, 0);
				break;
			}
			playerRoadPosition.put(player.getId(), 0);
		} else {
			RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(0);
			RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(1);
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
			playerRoadPosition.put(player.getId(), 1);
		}
		return vector;
	}
	
	private int getHighDistance(int distance) {
		int sum = 0;
		int value = 1;
		while(sum + value <= distance) {
			sum += value;
			value++;
		}
		return value - 1;
	}

	@Override
	public Position getStartingPosition() {
		List<Position> list = mapManager.getStartingPositionList();
		if (mapManager.getRoadDirectionInformationList().size() == 1) {
			return list.remove(0);
		}
		int index = 0;
		Position middlePosition = new Position((list.get(0).getX() + list.get(list.size() - 1).getX()) / 2, (list.get(0).getY() + list.get(list.size() - 1).getY()) / 2);
		int indexOf = list.indexOf(middlePosition);
		if (indexOf == -1) {
			indexOf = list.size() / 2;
		}
		switch(mapManager.getRoadDirectionInformationList().get(0).getOrientation()) {
		case NORTH:
			if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.EAST) {
				if (list.size() > 3) {
					index = 3;
				} else {
					index = list.size() - 1;
				}
			} else if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.WEST) {
				if (list.size() > 3) {
					index = 1;
				} else {
					index = 0;
				}
			}
			break;
		case WEST:
			if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.NORTH) {
				if (list.size() > 3) {
					index = 3;
				} else {
					index = list.size() - 1;
				}
			} else if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.SOUTH) {
				if (list.size() > 3) {
					index = 1;
				} else {
					index = 0;
				}
			}
			break;
		case SOUTH:
			if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.EAST) {
				if (list.size() > 3) {
					index = 1;
				} else {
					index = 0;
				}
			} else if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.WEST) {
				if (list.size() > 3) {
					index = 3;
				} else {
					index = list.size() - 1;
				}
			}
			break;
		case EAST:
			if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.NORTH) {
				if (list.size() > 3) {
					index = 1;
				} else {
					index = 0;
				}
			} else if (mapManager.getRoadDirectionInformationList().get(1).getOrientation() == Orientation.SOUTH) {
				if (list.size() > 3) {
					index = 3;
				} else {
					index = list.size() - 1;
				}
			}
			break;
		}
		
		return list.remove(index);
	}
}
