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
public final class AIHighLevel2 implements AILevel {

	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AIHighLevel2.class);
	private boolean isLastPlay = false;

	public AIHighLevel2(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) {
		int roadPosition = playerRoadPosition.get(player.getId());
		RoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(roadPosition);
		
		int len = r.getLengthToEnd(player.getPosition()) - 1;
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		List<Vector> solutionList = getVectorsPossibilities(player);
		if (solutionList.isEmpty()) {
			return null;
		}
		boolean isLastDirection = mapManager.getRoadDirectionInformationList().size() == roadPosition + 1;
		if (len > 3 || isLastDirection) {
			switch (r.getOrientation()) {
			case NORTH:
				int d = getNextPlay(len, player.getVector().getY());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(0, player.getVector().getY() + d);
				break;
			case SOUTH:
				d = getNextPlay(len, player.getVector().getY());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(0, player.getVector().getY() - d);
				break;
			case WEST:
				d = getNextPlay(len, player.getVector().getX());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() - d, 0);
				break;
			case EAST:
				d = getNextPlay(len, player.getVector().getX());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() + d, 0);
				break;
			}
			if (solutionList.contains(vector)) {
				return vector;
			}
			Vector tmpVector = null;
			for (Vector v : solutionList) {
				switch (r.getOrientation()) {
				case NORTH:
					if (v.getY() == vector.getY()) {
						tmpVector = v;
					}
					break;
				case SOUTH:
					if (v.getY() == vector.getY()) {
						tmpVector = v;
					}
					break;
				case WEST:
					if (v.getX() == vector.getX()) {
						tmpVector = v;
					}
					break;
				case EAST:
					if (v.getX() == vector.getX()) {
						tmpVector = v;
					}
					break;
				}
			}
			if (tmpVector != null) {
				return tmpVector;
			}
			int min = Integer.MAX_VALUE;
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
			return tmpVector;
		}
		Position endPosition = r.getEnd();
		for (Vector v : solutionList) {
			Position tmp = new Position(player.getPosition().getX() + v.getX(), player.getPosition().getY() - v.getY());
			if (endPosition.equals(tmp) && !mapManager.getCase(tmp.getY(), tmp.getX()).isOccuped()) {
				playerRoadPosition.put(player.getId(), roadPosition + 1);
				return v;
			}
		}
		
		RoadDirectionInformation nextRoadInformation = null;
		if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
			nextRoadInformation = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
		}
		for (Vector v : solutionList) {
			Position tmp = new Position(player.getPosition().getX() + v.getX(), player.getPosition().getY() - v.getY());
			if (!endPosition.equals(tmp) && !mapManager.getCase(tmp.getY(), tmp.getX()).isOccuped()) {
				List<Vector> list = getVectorsPossibilities(tmp, v);
				for (Vector vect : list) {
					Position tmpPos = new Position(tmp.getX() + vect.getX(), tmp.getY() - vect.getY());
					switch (nextRoadInformation.getOrientation()) {
					case NORTH:
						if (tmpPos.getY() == r.getEnd().getY()) {
							vector = v;
						}
						break;
					case SOUTH:
						if (tmpPos.getY() == r.getEnd().getY()) {
							vector = v;
						}
						break;
					case WEST:
						if (tmpPos.getX() == r.getEnd().getX()) {
							vector = v;
						}
						break;
					case EAST:
						if (tmpPos.getX() == r.getEnd().getX()) {
							vector = v;
						}
						break;
					}
				}
			}
			
		}
		playerRoadPosition.put(player.getId(), roadPosition + 1);
		return vector;
	}
	
	/**
	 * Return the adding length of the vector to run as fastest as possible the straight line.
	 * @param distance the total distance to run
	 * @param vitesse the current speed.
	 * @return the number to add to the current vector.
	 */
	private int getNextPlay(int distance, int vitesse) {
		log.trace("getNextPlay({}, {})", distance, vitesse);
		int v = Math.abs(vitesse);
		if (distance == 0) {
			if (v == 2) {
				return -1;
			}
			return vitesse;
		}
		if (distance > vitesse * vitesse) {
			return 1;
		}
		int nbLess = getNbStep(distance, v - 1, 0);
		int nbEqual = getNbStep(distance, v, 0);
		int nbMore = getNbStep(distance, v + 1, 0);
		log.debug("Next play possibilities : {}, {}, {}", new Object[]{nbLess, nbEqual, nbMore});
		if (nbLess <= nbEqual && nbLess <= nbMore) {
			return -1;
		} else if (nbMore < nbEqual && nbMore < nbLess) {
			return 1;
		}
		return 0;
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
		List<Vector> list = new ArrayList<Vector>();
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
		Vector vector = null;

		int val = getFirstMove(len);
		log.trace("length of the first move found : {}", val);

		if (val != 1) {
			if (mapManager.getRoadDirectionInformationList().size() == 1) {
				switch (mapManager.getRoadDirectionInformationList().get(0).getOrientation()) {
				case NORTH:
					vector = new Vector(0, mapManager.getMapSize());
					break;
				case WEST:
					vector = new Vector(-mapManager.getMapSize(), 0);
					break;
				case SOUTH:
					vector = new Vector(0, -mapManager.getMapSize());
					break;
				case EAST:
					vector = new Vector(mapManager.getMapSize(), 0);
					break;
				}
				playerRoadPosition.put(player.getId(), 0);
			} else {
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
				
				Position startPosition = player.getPosition();
				CaseModel model = mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() + vector.getX());
				if (model.isOccuped()) {
					//Il serait judicieux de prendre la décision suivant le prochain virage......
					switch (mapManager.getRoadDirectionInformationList().get(0).getOrientation()) {
					case NORTH:
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).isOccuped()) {
							vector = new Vector(-1, val);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).isOccuped()) {
							vector = new Vector(1,  val);
						}
					case SOUTH:
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).isOccuped()) {
							vector = new Vector(-1, -val);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).isOccuped()) {
							vector = new Vector(1, -val);
						}
						break;
					case WEST:
						if (mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(-val, -1);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(-val, 1);
						}
						break;
					case EAST:
						if (mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(val, -1);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(val, 1);
						}
						break;
					}
					
				}
			}
		} else {
			
//			RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(0);
//			RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(1);
			RoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(0);
			RoadDirectionInformation nextRoadDirection = mapManager.getDetailledRoadDirectionInformationList().get(1);
			int curveLen = nextRoadDirection.getLength();
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
		int index = Math.round(list.size() / 2);
		return list.remove(index);
	}
}
