package fr.ickik.formulamath.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;

/**
 * This class mades statistics about the player given in contructor's argument.
 * It gives averageDistance on every turn, the variance and the square type about
 * the run. It gives the number of every vector played.
 * @author Ickik
 * @version 0.1.001, 6 June 2012
 */
public final class Stats {

	private double averageDistance;
	private double variance;
	private double squareType;
	private final Player player;
	private final Map<Vector, Integer> vectorCountMap = new HashMap<Vector, Integer>();
	private static final Logger log = LoggerFactory.getLogger(Stats.class);
	
	/**
	 * Constructor of this class. It calculates stats about the player given in argument.
	 * @param player the player for which calculates the stats.
	 */
	public Stats(Player player) {
		log.debug("Initializes stats model for player {} ( {} )", player.getName(), player.getId());
		this.player = player;
		log.debug("Number of move : {}", Integer.toString(player.getMovingList().size()));
		treatment();
	}

	private void treatment() {
		HashMap<Double, Integer> distanceCountMap = new HashMap<Double, Integer>();
		double distance = 0;
		for (Vector v : player.getMovingList()) {
			double d = getDistance(v);
			distance += d;
			Integer value = vectorCountMap.get(v);
			if (value != null) {
				vectorCountMap.put(v, value + 1);
			} else {
				vectorCountMap.put(v, 1);
			}
			Integer nb = distanceCountMap.get(d);
			if (nb != null) {
				distanceCountMap.put(d, nb + 1);
			} else {
				distanceCountMap.put(d, 1);
			}
		}
		averageDistance = distance / player.getMovingList().size();
		
		double tmpVariance = 0;
		for (Double d : distanceCountMap.keySet()) {
			double difference = d - averageDistance;
			tmpVariance += (difference * difference * distanceCountMap.get(d));
		}
		variance = tmpVariance / player.getMovingList().size();
		squareType = Math.sqrt(variance);
	}
	
	private double getDistance(Vector vector) {
		return Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2));
	}
	
	/**
	 * Get the average distance moved on every turn.
	 * @return the average distance moved.
	 */
	public double getAverageDistance() {
		return averageDistance;
	}
	
	/**
	 * The variance of distance is the square of the square type representing the dispersion
	 * of distance played.
	 * @return the variance.
	 */
	public double getVariance() {
		return variance;
	}
	
	/**
	 * Return the square type of the moves for the run. The square type is the dispersion
	 * of player near the average distance.
	 * @return the square type.
	 */
	public double getSquareType() {
		return squareType;
	}
	
	/**
	 * Return the count of every {@link Vector} played on the run.
	 * @return the count of every vector played.
	 */
	public Map<Vector, Integer> getVectorCountMap() {
		return vectorCountMap;
	}

	/**
	 * Number of move in the race.
	 * @return the number of move.
	 */
	public int getMoveNumber() {
		return player.getMovingList().size();
	}
	
	/**
	 * Return the player concerned by this {@link Stats} object.
	 * @return the player for this the statistics are calculated.
	 */
	public Player getPlayer() {
		return player;
	}
}
