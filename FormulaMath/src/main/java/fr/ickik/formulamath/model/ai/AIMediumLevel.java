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
 * Class implements a medium level computer intelligence.
 * @author Ickik
 * @version 0.1.000, 18 July 2012
 * @since 0.3.9
 */
public final class AIMediumLevel implements AILevel {

	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AIMediumLevel.class);
	private boolean isLastPlay = false;

	public AIMediumLevel(MapManager mapManager) {
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
		} else if ((player.getVector().getX() == 1 ||  player.getVector().getX() == -1) && (player.getVector().getY() == 1 || player.getVector().getY() == -1)) {
			RoadDirectionInformation nextRoadDirection;
			if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
			} else {
				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
			}
			log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
			switch (r.getOrientation()) {
			case NORTH:
				vector = new Vector(0, 1);
				break;
			case SOUTH:
				vector = new Vector(0, -1);
				break;
			case WEST:
				vector = new Vector(-1, 0);
				break;
			case EAST:
				vector = new Vector(1, 0);
				break;
			}
		} else {
			log.debug("Go in the same direction {}", r.getOrientation());
			if (roadPosition == mapManager.getRoadDirectionInformationList().size() - 1) {
				log.trace("Last direction, final sprint");
				switch (r.getOrientation()) {
				case NORTH:
					vector = new Vector(0, player.getVector().getY() + 1);
					break;
				case SOUTH:
					vector = new Vector(0, player.getVector().getY() - 1);
					break;
				case WEST:
					vector = new Vector(player.getVector().getX() - 1, 0);
					break;
				case EAST:
					vector = new Vector(player.getVector().getX() + 1, 0);
					break;
				}
				if (len < Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY())) {
					log.trace("Last direction, last play, the AI will finished with {}", vector.toString());
					isLastPlay = true;
					return vector;
				}
			} else {
				log.debug("Normal way, orientation {}", r.getOrientation().toString());
				switch (r.getOrientation()) {
				case NORTH:
					int d = getNextPlay(len, player.getVector().getY());
					log.debug("Next play : {}", d);
					vector = new Vector(0, player.getVector().getY() + d);
					break;
				case SOUTH:
					d = getNextPlay(len, player.getVector().getY());
					log.debug("Next play : {}", d);
					vector = new Vector(0, player.getVector().getY() - d);
					break;
				case WEST:
					d = getNextPlay(len, player.getVector().getX());
					log.debug("Next play : {}", d);
					vector = new Vector(player.getVector().getX() - d, 0);
					break;
				case EAST:
					d = getNextPlay(len, player.getVector().getX());
					log.debug("Next play : {}", d);
					vector = new Vector(player.getVector().getX() + d, 0);
					break;
				}
				
			}
		}
		/*log.trace("Vector found : {}", vector);
		if (solutionList.indexOf(vector) != -1) {
			log.debug("Solution list did not found the vector in possible solution, the old vector will be instaciate");
			vector = player.getVector();
		}
		CaseModel model = mapManager.getCase(player.getPosition().getY() - vector.getY(), player.getPosition().getX()  + vector.getX());
		if (model != null && model.isOccuped()) {
			List<Vector> list = getVectorsPossibilities(player);
			if (list.isEmpty()) {
				log.debug("Solution list is empty");
				return null;
			}
			double defaultLength = getLength(player.getVector());
			log.trace("Length of the previous vector: {}", Double.toString(defaultLength));
			for (Vector v : list) {
				CaseModel m = mapManager.getCase(player.getPosition().getY() - v.getY(), player.getPosition().getX()  + v.getX());
				double length = getLength(v);
				if (m != null && !m.isOccuped() && defaultLength >= length) {
					log.debug("Case is not occuped, it will be used : {}", v);
					vector = v;
					break;
				}
			}
		}*/
		return vector;
	}

//	@Override
//	public Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) {
//		int roadPosition = playerRoadPosition.get(player.getId());
//		RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
//		int len = r.getLengthToEnd(player.getPosition()) - 1;
//		Vector vector = null;
//		log.debug("AI rest length of the vector:{}", len);
//		log.trace("Orientation: {}", r.getOrientation());
//		if ((Math.abs(len) == 1 || len == 0) && (player.getVector().getX() == 1 ||  player.getVector().getX() == -1 || player.getVector().getY() == 1 || player.getVector().getY() == -1)) {
//			RoadDirectionInformation nextRoadDirection;
//			if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
//				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
//			} else {
//				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
//			}
//			playerRoadPosition.put(player.getId(), roadPosition + 1);
//			log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
//			switch (r.getOrientation()) {
//			case NORTH:
//				if (nextRoadDirection.getOrientation() == Orientation.EAST) {
//					vector = new Vector(1, 1);
//				} else {
//					vector = new Vector(-1, 1);
//				}
//				break;
//			case SOUTH:
//				if (nextRoadDirection.getOrientation() == Orientation.EAST) {
//					vector = new Vector(1, -1);
//				} else {
//					vector = new Vector(-1, -1);
//				}
//				break;
//			case WEST:
//				if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
//					vector = new Vector(-1, 1);
//				} else {
//					vector = new Vector(-1, -1);
//				}
//				break;
//			case EAST:
//				if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
//					vector = new Vector(1, 1);
//				} else {
//					vector = new Vector(1, -1);
//				}
//				break;
//			}
//		} else if ((player.getVector().getX() == 1 ||  player.getVector().getX() == -1) && (player.getVector().getY() == 1 || player.getVector().getY() == -1)) {
//			RoadDirectionInformation nextRoadDirection;
//			if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
//				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
//			} else {
//				nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
//			}
//			log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
//			switch (r.getOrientation()) {
//			case NORTH:
//				vector = new Vector(0, 1);
//				break;
//			case SOUTH:
//				vector = new Vector(0, -1);
//				break;
//			case WEST:
//				vector = new Vector(-1, 0);
//				break;
//			case EAST:
//				vector = new Vector(1, 0);
//				break;
//			}
//		} else {
//			log.debug("Go in the same direction {}", r.getOrientation());
//			if (roadPosition == mapManager.getRoadDirectionInformationList().size() - 1) {
//				log.trace("Last direction, final sprint");
//				switch (r.getOrientation()) {
//				case NORTH:
//					vector = new Vector(0, player.getVector().getY() + 1);
//					break;
//				case SOUTH:
//					vector = new Vector(0, player.getVector().getY() - 1);
//					break;
//				case WEST:
//					vector = new Vector(player.getVector().getX() - 1, 0);
//					break;
//				case EAST:
//					vector = new Vector(player.getVector().getX() + 1, 0);
//					break;
//				}
//				if (len < Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY())) {
//					log.trace("Last direction, last play, the AI will finished with {}", vector.toString());
//					isLastPlay = true;
//					return vector;
//				}
//			} else {
//				log.debug("Normal way, orientation {}", r.getOrientation().toString());
//				switch (r.getOrientation()) {
//				case NORTH:
//					int d = getNextPlay(len, player.getVector().getY());
//					log.debug("Next play : {}", d);
//					vector = new Vector(0, player.getVector().getY() + d);
//					break;
//				case SOUTH:
//					d = getNextPlay(len, player.getVector().getY());
//					log.debug("Next play : {}", d);
//					vector = new Vector(0, player.getVector().getY() - d);
//					break;
//				case WEST:
//					d = getNextPlay(len, player.getVector().getX());
//					log.debug("Next play : {}", d);
//					vector = new Vector(player.getVector().getX() - d, 0);
//					break;
//				case EAST:
//					d = getNextPlay(len, player.getVector().getX());
//					log.debug("Next play : {}", d);
//					vector = new Vector(player.getVector().getX() + d, 0);
//					break;
//				}
//			}
//		}
//		CaseModel model = mapManager.getCase(player.getPosition().getY() - vector.getY(), player.getPosition().getX()  + vector.getX());
//		if (model != null && model.isOccuped()) {
//			List<Vector> list = getVectorsPossibilities(player);
//			if (list.isEmpty()) {
//				return null;
//			}
//			double defaultLength = getLength(player.getVector());
//			for (Vector v : list) {
//				CaseModel m = mapManager.getCase(player.getPosition().getY() - v.getY(), player.getPosition().getX()  + v.getX());
//				double length = getLength(v);
//				if (m != null && !m.isOccuped() && defaultLength >= length) {
//					vector = v;
//					break;
//				}
//			}
//		}
//		return vector;
//	}
	
	private double getLength(Vector vector) {
		return Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());
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
