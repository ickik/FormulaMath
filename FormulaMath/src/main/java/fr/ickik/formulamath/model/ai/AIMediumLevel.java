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

public class AIMediumLevel implements AILevel {

	private final MapManager mapManager;
	private final Map<Integer, Integer> playerRoadPosition = new HashMap<Integer, Integer>();
	private static final Logger log = LoggerFactory.getLogger(AIMediumLevel.class);

	public AIMediumLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}




	@Override
	public Vector getNextPlay(Player player) {
		log.debug("AI Player {} is under playing", player.toString());

		int roadPosition = playerRoadPosition.get(player.getId());
		RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
		int len = r.getLengthToEnd(player.getPosition());
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		if (len == 1 && (player.getVector().getX() == 1 ||  player.getVector().getX() == -1 || player.getVector().getY() == 1 || player.getVector().getY() == -1)) {
			RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
			//playerRoadPosition.put(p.getId(), roadPosition + 1);
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
			RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
			playerRoadPosition.put(player.getId(), roadPosition + 1);
			log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
			switch (r.getOrientation()) {
			case NORTH:
				if (nextRoadDirection.getOrientation() == Orientation.EAST) {
					vector = new Vector(1, 0);
				} else {
					vector = new Vector(-1, 0);
				}
				break;
			case SOUTH:
				if (nextRoadDirection.getOrientation() == Orientation.EAST) {
					vector = new Vector(-1, 0);
				} else {
					vector = new Vector(1, 0);
				}
				break;
			case WEST:
				if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
					vector = new Vector(0, 1);
				} else {
					vector = new Vector(0, -1);
				}
				break;
			case EAST:
				if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
					vector = new Vector(0, 1);
				} else {
					vector = new Vector(0, -1);
				}
				break;
			}
		} else {
			log.debug("Go in the same direction {}", r.getOrientation());
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
				vector = new Vector(player.getVector().getX() + d, 0);
				break;
			case EAST:
				d = getNextPlay(len, player.getVector().getX());
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() - d, 0);
				break;
			}
		}
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
		if (distance == 0) {
			if (vitesse == 2) {
				return -1;
			}
			return vitesse;
		}
		if (distance > vitesse * vitesse) {
			return 1;
		}
		int nbLess = getNbStep(distance, vitesse - 1, 0);
		int nbEqual = getNbStep(distance, vitesse, 0);
		int nbMore = getNbStep(distance, vitesse + 1, 0);
		log.debug("Next play possibilities : {}, {}, {}", new Object[]{nbLess, nbEqual, nbMore});
		if (nbLess < nbEqual && nbLess < nbMore) {
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
		if (distance == 0 && vitesse == 1) {
			return step;
		}
		if (distance < 0 || vitesse <= 0) {
			return Integer.MAX_VALUE;
		}
		step++;
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step);
		int nbMore = getNbStep(distance - vitesse, vitesse + 1, step);
		return Math.min(nbMore, Math.min(nbLess, nbEqual));
	}
}