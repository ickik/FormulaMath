package fr.ickik.formulamath.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ickik.formulamath.entity.Vector;

public class Stats {

	private double averageDistance;
	private double variance;
	private double squareType;
	private final Map<Vector, Integer> vectorCountMap = new HashMap<Vector, Integer>();
	
	private void treatment() {
		List<Vector> vectorList = null;
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
	
	public double getAverageDistance() {
		return averageDistance;
	}
	
	public double getVariance() {
		return variance;
	}
	
	public double squareType() {
		return squareType;
	}
	
	public Map<Vector, Integer> getVectorCountMap() {
		return vectorCountMap;
	}
}
