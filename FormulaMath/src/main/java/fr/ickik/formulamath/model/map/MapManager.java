package fr.ickik.formulamath.model.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.entity.RoadDirectionList;
import fr.ickik.formulamath.model.CaseModel;

/**
 * Contains and handles the map of the application. The map is a everytime a square.
 * @author Ickik.
 * @version 0.1.004, 22 mar. 2012.
 */
public class MapManager {

	public static final int ROAD_SIZE = 4;
	public static final int EMPTY_PLAYER = 0;
	private static final Logger log = LoggerFactory.getLogger(MapManager.class);
	private final List<List<CaseModel>> carte;
	private final int mapSize;
	private final List<Position> startingPositionList = new ArrayList<Position>(2);
	private final List<Position> finishingLinePositionList = new ArrayList<Position>(2);
	private final List<RoadDirectionInformation> roadList = new RoadDirectionList();

	/**
	 * Constructor of the map manager. It needs the size of the map.
	 * The size of the map is the number of case on one side of the square.
	 * @param size the number of cases of square's side.
	 */
	public MapManager(int size) {
		log.trace("Constructor begin");
		log.debug("Size of the map defined : {}", size);
		this.mapSize = size;
		carte = new ArrayList<List<CaseModel>>(size);
		init();
		constructRoad();
		log.debug("Road list operational : size {}",roadList.size());
		log.debug("{}", roadList.toString());
		log.trace("Constructor exiting");
	}

	private void init() {
		log.debug("Init begin");
		for (int i = 0; i < mapSize; i++) {
			List<CaseModel> list = new ArrayList<CaseModel>(mapSize);
			for (int j = 0; j < mapSize; j++) {
				list.add(new CaseModel());
			}
			carte.add(list);
		}
		log.debug("Map initialized");
	}

	private void initStartPosition(Position p, Position p2) {
		int minX = Math.min(p.getX(), p2.getX());
		int maxX = Math.max(p.getX(), p2.getX());
		int minY = Math.min(p.getY(), p2.getY());
		int maxY = Math.max(p.getY(), p2.getY());
		if (p.getX() == p2.getX()) {
			for (int i = minY; i <= maxY; i++) {
				log.debug("{}", (new Position(p.getX(), i).toString()));
				startingPositionList.add(new Position(p.getX(), i));
			}
		} else if (p.getY() == p2.getY()) {
			for (int i = minX; i <= maxX; i++) {
				log.debug("{}", (new Position(i, p.getY()).toString()));
				startingPositionList.add(new Position(i, p.getY()));
			}
		}
	}

	private void constructRoad() {
		final int curveLength = ROAD_SIZE + 1;
		boolean isFinished = false;
		int nbDirection = Direction.values().length;

		log.debug("constructRoad begin");
		Position positionDepart = new Position();
		Position positionDepart2 = new Position();
		Orientation coteDepart = traceStartingLine(positionDepart, positionDepart2);
		initStartPosition(positionDepart, positionDepart2);
		Position position = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
		roadList.add(new RoadDirectionInformation(coteDepart, position, position.clone()));

		Queue<Direction> previousDirection = new ArrayDeque<Direction>(2);
		while (!isFinished) {
			Direction direction = Direction.values()[getRandomNumber(nbDirection)];
			if (previousDirection.size() == 2) {
				int cpt = 0;
				for (Direction d : previousDirection) {
					if (direction == d) {
						cpt++;
					}
				}
				if (cpt == 2) {
					continue;
				}
				previousDirection.poll();
			}
			
			previousDirection.add(direction);
			switch (coteDepart) {
			case NORTH:
				switch (direction) {
				case LEFT:
					log.debug("{} => {} entering", coteDepart.name(),direction.name());
					if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
						log.debug("{} => {} entering", coteDepart.name(),direction.name());
						if (!checkNewDirection(Orientation.NORTH, Direction.LEFT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						positionDepart2.setX(positionDepart2.getX() + 1);
						positionDepart.setX(positionDepart2.getX());
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - ROAD_SIZE);
						Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
						log.debug("{} => {} exiting", coteDepart.name(),direction.name());
						coteDepart = Orientation.WEST;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(positionDepart.getY());
					log.debug("Length : {}", len);
					if (positionDepart.getY() - len < ROAD_SIZE) {
						len = positionDepart.getY() - 1;
						isFinished = true;
					}
					log.debug("Length : {}", len);
					if (!checkDirection(coteDepart, len, positionDepart.clone(), positionDepart2.clone())) {
						break;
					}
					Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					log.debug("length of the way = {}", len);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
					log.debug("{} => {} exiting", coteDepart.name(), direction.name());
					break;

				case RIGHT:
					if (positionDepart2.getX() > ROAD_SIZE + 1 && mapSize - positionDepart2.getY() > ROAD_SIZE) {
						log.debug(coteDepart.name() + " => " + direction.name() + " entering");
						if (!checkNewDirection(Orientation.NORTH, Direction.RIGHT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						positionDepart2.setX(positionDepart.getX() - 1);
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setY(positionDepart.getY() - 1);
						positionDepart.setY(positionDepart.getY() - ROAD_SIZE);
						centerDepart = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
						coteDepart = Orientation.EAST;
					}
					break;
				}
				break;

			case WEST:
				switch (direction) {
				case LEFT:
					log.debug(coteDepart.name() + " => " + direction.name() + " entering");
					if (positionDepart.getX() >= curveLength && mapSize - positionDepart.getY() >= curveLength) {
						if (!checkNewDirection(Orientation.WEST, Direction.LEFT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug(coteDepart.name() + " => " + direction.name());
						positionDepart2.setY(positionDepart2.getY() - 1);
						positionDepart.setY(positionDepart2.getY());
						positionDepart.setX(positionDepart2.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - ROAD_SIZE);
						Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() + 1) / 2);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() - 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.SOUTH;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(positionDepart.getX());
					log.debug("random length of the way = {}", len);
					if (positionDepart.getX() - len <= ROAD_SIZE) {
						len = positionDepart.getX() - 1;
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					if (len == 0) {
						isFinished = true;
						break;
					}
					if (!checkDirection(coteDepart, len, positionDepart.clone(), positionDepart2.clone())) {
						break;
					}
					Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					for (int i = 0; i < len; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
					log.debug(coteDepart.name() + " => " + direction.name() + " ended");
					break;

				case RIGHT:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
						if (!checkNewDirection(Orientation.WEST, Direction.RIGHT, positionDepart.clone(), positionDepart2.clone())) {
							log.debug("Refused by checkNewDirection");
							break;
						}
						log.debug(coteDepart.name() + " => " + direction.name());
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart.getY());
						positionDepart.setX(positionDepart.getX() - ROAD_SIZE);
						positionDepart2.setX(positionDepart2.getX() - 1);
						log.debug("Start Position1 [x = {}, y = {}]", positionDepart.getX(), positionDepart.getY());
						log.debug("Start Position2 [x = {}, y = {}]", positionDepart2.getX(), positionDepart2.getY());
						centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() - 1) / 2);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() + 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
						coteDepart = Orientation.NORTH;
					}
					break;
				}
				break;

			case SOUTH:
				switch (direction) {
				case LEFT:
					if (positionDepart.getX() < ROAD_SIZE + 1 && positionDepart.getY() < ROAD_SIZE + 1) {
						if (!checkNewDirection(Orientation.SOUTH, Direction.LEFT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart.setX(positionDepart2.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						positionDepart2.setY(positionDepart2.getY() + ROAD_SIZE);
						positionDepart.setY(positionDepart.getY() + 1);
						Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.EAST;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getY());
					log.debug("length of the way = {}", len);
					if (mapSize - positionDepart.getY() - len <= ROAD_SIZE) {
						len = mapSize - positionDepart.getY() - 2;
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					if (!checkDirection(coteDepart, len, positionDepart.clone(), positionDepart2.clone())) {
						break;
					}
					log.debug("length of the way = {}", len);
					Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
					log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
					break;

				case RIGHT:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (mapSize - positionDepart2.getX() > ROAD_SIZE && mapSize - positionDepart2.getY() > ROAD_SIZE) {
						if (!checkNewDirection(Orientation.SOUTH, Direction.RIGHT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart2.setX(positionDepart.getX() + 1);
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
						positionDepart2.setY(positionDepart2.getY() + 1);
						centerDepart = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.WEST;
					}
					break;
				}
				break;

			case EAST:
				switch (direction) {
				case LEFT:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (mapSize - positionDepart.getX() > ROAD_SIZE && positionDepart.getY() > ROAD_SIZE) {
						if (!checkNewDirection(Orientation.EAST, Direction.LEFT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart2.setY(positionDepart2.getY() + 1);
						positionDepart.setY(positionDepart2.getY());
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
						Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() - 1) / 2);
						
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() + 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
						log.debug("{} => {} exiting", coteDepart.name(), direction.name());
						coteDepart = Orientation.NORTH;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getX() - 1);
					log.trace("calculated length : {}", len);
					if (mapSize - positionDepart.getX() - len < ROAD_SIZE) {
						len = mapSize - positionDepart.getX() - 1;
						log.trace("length < Road size; new length : {}", len);
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					if (len == 0) {
						isFinished = true;
						break;
					}
					if (!checkDirection(coteDepart, len, positionDepart.clone(), positionDepart2.clone())) {
						break;
					}
					Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					for (int i = 0; i < len; i++) {
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
					log.debug("{} => {} exiting", coteDepart.name(), direction.name());
					break;

				case RIGHT:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (mapSize - positionDepart.getX() < ROAD_SIZE && mapSize - positionDepart.getY() < ROAD_SIZE) {
						if (!checkNewDirection(Orientation.EAST, Direction.RIGHT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart.getY());
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
						centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() + 1) / 2);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() - 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
						log.debug("{} => {} exiting", coteDepart.name(), direction.name());
						coteDepart = Orientation.SOUTH;
					}
					break;
				}
			}
		}
		traceFinishingLine(coteDepart, positionDepart, positionDepart2);
		log.debug("constructRoad end");
	}
	
	private boolean checkDirection(Orientation orientation, int length, Position positionDepart, Position positionDepart2) {
		boolean solutionAvailable = true;
		switch (orientation) {
		case NORTH:
			solutionAvailable = checkSolutionNorthSouth(-1, length, positionDepart, positionDepart2);
			break;

		case WEST:
			solutionAvailable = checkSolution(-1, length, positionDepart, positionDepart2);
			break;

		case SOUTH:
			solutionAvailable = checkSolutionNorthSouth(1, length, positionDepart, positionDepart2);
			break;

		case EAST:
			solutionAvailable = checkSolution(1, length, positionDepart, positionDepart2);
			break;
		}
		return solutionAvailable;
	}
	
	private boolean checkSolutionNorthSouth(int value, int length, Position positionDepart, Position positionDepart2) {
		boolean solutionAvailable = true;
		for (int i = 0; i < length; i++) {
			positionDepart.setY(positionDepart.getY() + value);
			positionDepart2.setY(positionDepart2.getY() + value);
			if (carte.get(positionDepart.getX()).get(positionDepart.getY()).getField() == Field.ROAD || carte.get(positionDepart2.getX()).get(positionDepart2.getY()).getField() == Field.ROAD) {
				solutionAvailable = false;
				log.debug("Solution not available at {} / {}", positionDepart.toString(), positionDepart2.toString());
				break;
			}
		}
		return solutionAvailable;
	}
	
	private boolean checkSolution(int value, int length, Position positionDepart, Position positionDepart2) {
		boolean solutionAvailable = true;
		for (int i = 0; i < length; i++) {
			positionDepart.setX(positionDepart.getX() + value);
			positionDepart2.setX(positionDepart2.getX() + value);
			if (carte.get(positionDepart.getX()).get(positionDepart.getY()).getField() == Field.ROAD || carte.get(positionDepart2.getX()).get(positionDepart2.getY()).getField() == Field.ROAD) {
				solutionAvailable = false;
				log.debug("Solution not available at {} / {}", positionDepart.toString(), positionDepart2.toString());
				break;
			}
		}
		return solutionAvailable;
	}
	
	private boolean checkNewDirection(Orientation orientation, Direction direction, Position positionDepart, Position positionDepart2) {
		final int curveLength = ROAD_SIZE + 1;
		boolean solutionAvailable = true;
		switch (orientation) {
		case NORTH:
			switch (direction) {
			case LEFT:
				if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
					positionDepart2.setX(positionDepart2.getX() + 1);
					positionDepart.setX(positionDepart2.getX());
					positionDepart.setY(positionDepart2.getY() - 1);
					positionDepart2.setY(positionDepart.getY() - ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				} else {
					solutionAvailable = false;
				}
				break;

			case RIGHT:
				if (positionDepart2.getX() > ROAD_SIZE + 1 && mapSize - positionDepart2.getY() > ROAD_SIZE) {
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart2.setX(positionDepart.getX());
					positionDepart2.setY(positionDepart.getY() + 1);
					positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				} else {
					solutionAvailable = false;
				}
				break;
			}
			break;

		case WEST:
			switch (direction) {
			case LEFT:
				if (positionDepart.getX() >= curveLength && mapSize - positionDepart.getY() >= curveLength) {
					positionDepart.setY(positionDepart2.getY() - 1);
					positionDepart2.setY(positionDepart2.getY() - 1);
					positionDepart.setX(positionDepart.getX() - 1);
					positionDepart2.setX(positionDepart2.getX() - ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;

			case RIGHT:
				if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
					positionDepart.setY(positionDepart.getY() + 1);
					positionDepart2.setY(positionDepart.getY());
					positionDepart.setX(positionDepart.getX() - ROAD_SIZE);
					positionDepart2.setX(positionDepart2.getX() - 1);
					log.debug("Start Position1 [x = {}, y = {}]", positionDepart.getX(), positionDepart.getY());
					log.debug("Start Position2 [x = {}, y = {}]", positionDepart2.getX(), positionDepart2.getY());
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;
			}
			break;

		case SOUTH:
			switch (direction) {
			case LEFT:
				if (mapSize - positionDepart.getX() < ROAD_SIZE + 1 && mapSize - positionDepart.getY() < ROAD_SIZE + 1) {
					positionDepart.setX(positionDepart2.getX() - 1);
					positionDepart2.setX(positionDepart2.getX() - 1);
					positionDepart2.setY(positionDepart2.getY() + ROAD_SIZE);
					positionDepart.setY(positionDepart.getY() + 1);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;

			case RIGHT:
				if (positionDepart2.getX() > ROAD_SIZE + 1 && positionDepart2.getY() > ROAD_SIZE + 1) {
					positionDepart2.setX(positionDepart.getX() + 1);
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
					positionDepart2.setY(positionDepart2.getY() + 1);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;
			}
			break;

		case EAST:
			switch (direction) {
			case LEFT:
				if (mapSize - positionDepart.getX() < ROAD_SIZE && mapSize - positionDepart.getY() < ROAD_SIZE) {
					positionDepart2.setY(positionDepart2.getY() + 1);
					positionDepart.setY(positionDepart2.getY());
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;

			case RIGHT:
				if (positionDepart.getX() < ROAD_SIZE + 1 && positionDepart.getY() < ROAD_SIZE + 1) {
					positionDepart.setY(positionDepart.getY() - 1);
					positionDepart2.setY(positionDepart.getY());
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;
			}
		}
		return solutionAvailable;
	}

	private Orientation traceStartingLine(Position positionDepart, Position positionDepart2) {
		log.debug("traceLigneDepart entering");
		int posDepart = getRandomNumber(1, mapSize - ROAD_SIZE);

		Orientation coteDepart = Orientation.values()[getRandomNumber(Orientation.values().length)];
		log.debug("start position depart : " + posDepart + ", direction : "	+ coteDepart);
		switch (coteDepart) {
		case NORTH:
			positionDepart.setX(posDepart);
			positionDepart2.setX(posDepart + ROAD_SIZE - 1);
			positionDepart.setY(mapSize - 1);
			positionDepart2.setY(mapSize - 1);
			break;
		case EAST:
			positionDepart.setY(posDepart);
			positionDepart2.setY(posDepart + ROAD_SIZE - 1);
			break;
		case SOUTH:
			positionDepart.setX(posDepart + ROAD_SIZE - 1);
			positionDepart2.setX(posDepart);
			break;
		case WEST:
			positionDepart.setX(mapSize - 1);
			positionDepart2.setX(mapSize - 1);
			positionDepart.setY(posDepart + ROAD_SIZE - 1);
			positionDepart2.setY(posDepart);
			break;
		}
		log.debug("PositionDepart x=" + positionDepart.getX() + ", y=" + positionDepart.getY());
		log.debug("PositionDepart2 x=" + positionDepart2.getX() + ", y=" + positionDepart2.getY());
		traceLargeur(positionDepart, positionDepart2, Field.STARTING_LINE);
		log.debug("traceLigneDepart exiting");
		return coteDepart;
	}
	
	private void traceFinishingLine(Orientation coteDepart, Position positionDepart, Position positionDepart2) {
		log.debug("Trace end line {}, {}", positionDepart.toString(), positionDepart2.toString());
		switch (coteDepart) {
		case NORTH:
			positionDepart.setY(positionDepart.getY() - 1);
			positionDepart2.setY(positionDepart2.getY() - 1);
			break;
		case EAST:
			positionDepart.setX(positionDepart.getX() + 1);
			positionDepart2.setX(positionDepart2.getX() + 1);
			break;
		case SOUTH:
			positionDepart.setY(positionDepart.getY() + 1);
			positionDepart2.setY(positionDepart2.getY() + 1);
			break;
		case WEST:
			positionDepart.setX(positionDepart.getX() - 1);
			positionDepart2.setX(positionDepart2.getX() - 1);
			break;
		}
		finishingLinePositionList.add(positionDepart);
		finishingLinePositionList.add(positionDepart2);
		traceLargeur(positionDepart, positionDepart2, Field.FINISHING_LINE);
	}

	private void traceLargeur(Position positionDepart, Position positionDepart2) {
		traceLargeur(positionDepart, positionDepart2, Field.ROAD);
	}

	private void traceLargeur(Position positionDepart,Position positionDepart2, Field terrain) {
		log.debug("traceLargeur begin");
		log.debug("Start Position1 [x = {}, y = {}]", positionDepart.getX(), positionDepart.getY());
		log.debug("Start Position2 [x = {}, y = {}]", positionDepart2.getX(), positionDepart2.getY());
		int minX = Math.min(positionDepart.getX(), positionDepart2.getX());
		int maxX = Math.max(positionDepart.getX(), positionDepart2.getX());
		int minY = Math.min(positionDepart.getY(), positionDepart2.getY());
		int maxY = Math.max(positionDepart.getY(), positionDepart2.getY());
		if (positionDepart.getX() == positionDepart2.getX()) {
			for (int i = minY; i <= maxY; i++) {
				carte.get(i).get(positionDepart.getX()).setField(terrain);
			}
		}

		if (positionDepart.getY() == positionDepart2.getY()) {
			for (int i = minX; i <= maxX; i++) {
				carte.get(positionDepart.getY()).get(i).setField(terrain);
			}
		}
		log.debug("traceLargeur exiting");
	}

	@Override
	public String toString() {
		StringBuilder display = new StringBuilder("\n");
		for (int i = 0; i < mapSize; i++) {
			StringBuilder str = new StringBuilder();
			for (CaseModel c : carte.get(i)) {
				if (c.getField() == Field.GRASS) {
					str.append(0);
				} else {
					str.append(1);
				}
			}
			display.append(str);
			display.append("\n");
		}
		return display.toString();
	}

	public int getMapSize() {
		return mapSize;
	}

	private int getRandomNumber(int min, int max) {
		Random r = new Random();
		return r.nextInt(max - min) + min;
	}

	private int getRandomNumber(int max) {
		if (max == 0) {
			return 0;
		}
		return getRandomNumber(0, max);
	}

	public CaseModel getCase(int h, int w) {
		if (h < 0 || h >= carte.size() || w < 0 || w >= carte.get(h).size()) {
			return null;
		}
		return carte.get(h).get(w);
	}

	public List<Position> getStartingPositionList() {
		return startingPositionList;
	}
	
	public List<Position> getFinishingLinePositionList() {
		return finishingLinePositionList;
	}
	
	public List<RoadDirectionInformation> getRoadDirectionInformationList() {
		return roadList;
	}
}
