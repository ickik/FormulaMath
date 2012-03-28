package fr.ickik.formulamath.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;

/**
 * This class mades statistics about the player given in contructor's argument.
 * It gives averageDistance on every turn, the variance and the square type about
 * the run. It gives the number of every vector played.
 * @author Patrick Allgeyer
 * @version 0.1.000, 22 mar. 2012
 */
public final class Stats {

	private double averageDistance;
	private double variance;
	private double squareType;
	private final Map<Vector, Integer> vectorCountMap = new HashMap<Vector, Integer>();
	
	/**
	 * Constructor of this class. It calculates stats about the player given in argument.
	 * @param player the player for which calculates the stats.
	 */
	public Stats(Player player) {
		treatment(player);
	}

	private void treatment(Player player) {
		List<Vector> vectorList = player.getMovingList();
		HashMap<Double, Integer> distanceCountMap = new HashMap<Double, Integer>();
		double distance = 0;
		for (Vector v : vectorList) {
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
		averageDistance = distance / vectorList.size();
		
		double tmpVariance = 0;
		for (Double d : distanceCountMap.keySet()) {
			double difference = d - averageDistance;
			tmpVariance += (difference * difference * distanceCountMap.get(d));
		}
		variance = tmpVariance / vectorList.size();
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
	 * The variance of distance is the square of the square type representing the dispertion
	 * of distance played.
	 * @return the variance.
	 */
	public double getVariance() {
		return variance;
	}
	
	/**
	 * Return the square type of the moves for the run. The square type is the dispertion
	 * of player near the average distance.
	 * @return the sqaure type.
	 */
	public double getSquareType() {
		return squareType;
	}
	
	/**
	 * Return the count of every {@link Vector} played on the run.
	 * @return the vount of every vector played.
	 */
	public Map<Vector, Integer> getVectorCountMap() {
		return vectorCountMap;
	}
}
