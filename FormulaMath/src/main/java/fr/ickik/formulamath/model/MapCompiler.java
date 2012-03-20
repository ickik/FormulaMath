package fr.ickik.formulamath.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


public class MapCompiler extends TimerTask {

	private int[][] map;
	private List<Integer> startLaneList = new ArrayList<Integer>();
	private List<Integer> finishLaneList = new ArrayList<Integer>();
	
	public MapCompiler() {

	}

	@Override
	public void run() {
		StringBuilder str = new StringBuilder();
		if (!isStartLane()) {
			str.append("No starting lane defined\n");
		}
		if (!isFinishLane()) {
			str.append("No finishing lane defined\n");
		}
		if (!checkContinuity()) {
			str.append("Error");
		}
	}

	private boolean isStartLane() {
		return !startLaneList.isEmpty();
	}

	private boolean isFinishLane() {
		return !finishLaneList.isEmpty();
	}

	private boolean checkContinuity() {
		return false;
	}

	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}
}
