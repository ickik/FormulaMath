package fr.ickik.formulamath.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.Case;
import fr.ickik.formulamath.Direction;
import fr.ickik.formulamath.Orientation;
import fr.ickik.formulamath.Position;
import fr.ickik.formulamath.Terrain;

/**
 * Contains and handles the map of the application. The map is a everytime a square.
 * @author Ickik.
 * @version 0.1.001, 3 nov. 2011.
 */
public class MapManager {

	private final List<List<Case>> carte;
	private final int mapSize;
	private final int ROAD_SIZE = 4;
	private final List<Position> startPositionList = new ArrayList<Position>(2);
	private final List<Position> endLinePositionList = new ArrayList<Position>(2);
	public static final int EMPTY_PLAYER = 0;
	
	private static final Logger log = LoggerFactory.getLogger(MapManager.class);

	/**
	 * Constructor of the map manager. It needs the size of the map.
	 * The size of the map is the number of case on one side of the square.
	 * @param size the number of cases of square's side.
	 */
	public MapManager(int size) {
		log.debug("Constructor begin");
		this.mapSize = size;
		carte = new ArrayList<List<Case>>(size);
		init();
		constructRoad();
		log.debug("Constructor exiting");
	}

	private void init() {
		log.debug("Init begin");
		for (int i = 0; i < mapSize; i++) {
			List<Case> list = new ArrayList<Case>(mapSize);
			for (int j = 0; j < mapSize; j++) {
				list.add(new Case(Terrain.HERBE));
			}
			carte.add(list);
		}
		log.debug("Init exiting");
	}

	private void initStartPosition(Position p, Position p2) {
		int minX = Math.min(p.getX(), p2.getX());
		int maxX = Math.max(p.getX(), p2.getX());
		int minY = Math.min(p.getY(), p2.getY());
		int maxY = Math.max(p.getY(), p2.getY());
		if (p.getX() == p2.getX()) {
			for (int i = minY; i <= maxY; i++) {
				log.debug("{}", (new Position(p.getX(), i).toString()));
				startPositionList.add(new Position(p.getX(), i));
			}
		}

		if (p.getY() == p2.getY()) {
			for (int i = minX; i <= maxX; i++) {
				log.debug("{}", (new Position(i, p.getY()).toString()));
				startPositionList.add(new Position(i, p.getY()));
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
		Orientation coteDepart = traceStartLine(positionDepart, positionDepart2);

		initStartPosition(positionDepart, positionDepart2);

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
			case NORD:
				switch (direction) {
				case GAUCHE:
					log.debug("{} => {} entering", coteDepart.name(),direction.name());
					if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
						log.debug("{} => {} entering", coteDepart.name(),direction.name());
						if (!checkNewDirection(Orientation.NORD, Direction.GAUCHE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						positionDepart2.setX(positionDepart2.getX() + 1);
						positionDepart.setX(positionDepart2.getX());
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - ROAD_SIZE);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug("{} => {} exiting", coteDepart.name(),direction.name());
						coteDepart = Orientation.OUEST;
					}
					break;

				case MILIEU:
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
					log.debug("length of the way = {}", len);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug("{} => {} exiting", coteDepart.name(), direction.name());
					break;

				case DROITE:
					if (positionDepart2.getX() > ROAD_SIZE + 1 && mapSize - positionDepart2.getY() > ROAD_SIZE) {
						log.debug(coteDepart.name() + " => " + direction.name() + " entering");
						if (!checkNewDirection(Orientation.NORD, Direction.DROITE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						positionDepart2.setX(positionDepart.getX() - 1);
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setY(positionDepart.getY() - 1);
						positionDepart.setY(positionDepart.getY() - ROAD_SIZE);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.EST;
					}
					break;
				}
				break;

			case OUEST:
				switch (direction) {
				case GAUCHE:
					log.debug(coteDepart.name() + " => " + direction.name() + " entering");
					if (positionDepart.getX() >= curveLength && mapSize - positionDepart.getY() >= curveLength) {
						if (!checkNewDirection(Orientation.OUEST, Direction.GAUCHE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug(coteDepart.name() + " => " + direction.name());
						positionDepart2.setY(positionDepart2.getY() - 1);
						positionDepart.setY(positionDepart2.getY());
						positionDepart.setX(positionDepart2.getX() - 1);
						positionDepart2.setX(positionDepart.getX() - ROAD_SIZE);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.SUD;
					}
					break;

				case MILIEU:
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
					for (int i = 0; i < len; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug(coteDepart.name() + " => " + direction.name() + " ended");
					break;

				case DROITE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
						if (!checkNewDirection(Orientation.OUEST, Direction.DROITE, positionDepart.clone(), positionDepart2.clone())) {
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
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						coteDepart = Orientation.NORD;
					}
					break;
				}
				break;

			case SUD:
				switch (direction) {
				case GAUCHE:
					if (positionDepart.getX() < ROAD_SIZE + 1 && positionDepart.getY() < ROAD_SIZE + 1) {
						if (!checkNewDirection(Orientation.SUD, Direction.GAUCHE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart.setX(positionDepart2.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						positionDepart2.setY(positionDepart2.getY() + ROAD_SIZE);
						positionDepart.setY(positionDepart.getY() + 1);
						
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.EST;
					}
					break;

				case MILIEU:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getY());
					log.debug("length of the way = {}", len);
					if (mapSize - positionDepart.getY() - len <= ROAD_SIZE) {
						len = mapSize - positionDepart.getY() - 1;
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					if (!checkDirection(coteDepart, len, positionDepart.clone(), positionDepart2.clone())) {
						break;
					}
					log.debug("length of the way = {}", len);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
					break;

				case DROITE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (mapSize - positionDepart2.getX() > ROAD_SIZE && mapSize - positionDepart2.getY() > ROAD_SIZE) {
						if (!checkNewDirection(Orientation.SUD, Direction.DROITE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart2.setX(positionDepart.getX() + 1);
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
						positionDepart2.setY(positionDepart2.getY() + 1);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.OUEST;
					}
					break;
				}
				break;

			case EST:
				switch (direction) {
				case GAUCHE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (mapSize - positionDepart.getX() > ROAD_SIZE && positionDepart.getY() > ROAD_SIZE) {
						if (!checkNewDirection(Orientation.EST, Direction.GAUCHE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart2.setY(positionDepart2.getY() + 1);
						positionDepart.setY(positionDepart2.getY());
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
						
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug("{} => {} exiting", coteDepart.name(), direction.name());
						coteDepart = Orientation.NORD;
					}
					break;

				case MILIEU:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getX());
					if (mapSize - positionDepart.getX() - len < ROAD_SIZE) {
						len = mapSize - positionDepart.getX() - 1;
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
					for (int i = 0; i < len; i++) {
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug("{} => {} exiting", coteDepart.name(), direction.name());
					break;

				case DROITE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (mapSize - positionDepart.getX() < ROAD_SIZE && mapSize - positionDepart.getY() < ROAD_SIZE) {
						if (!checkNewDirection(Orientation.EST, Direction.DROITE, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart.getY());
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug("{} => {} exiting", coteDepart.name(),
								direction.name());
						coteDepart = Orientation.SUD;
					}
					break;
				}
			}
		}
		traceEndLine(coteDepart, positionDepart, positionDepart2);
		log.debug("constructRoad end");
	}
	
	private boolean checkDirection(Orientation orientation, int length, Position positionDepart, Position positionDepart2) {
		boolean solutionAvailable = true;
		switch (orientation) {
		case NORD:
			if (positionDepart.getY() - length < ROAD_SIZE) {
				solutionAvailable = false;
				break;
			}
			log.debug("length of the way = {}", length);
			for (int i = 0; i < length; i++) {
				positionDepart.setY(positionDepart.getY() - 1);
				positionDepart2.setY(positionDepart2.getY() - 1);
				if (carte.get(positionDepart.getX()).get(positionDepart.getY()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getX()).get(positionDepart2.getY()).getTerrain() == Terrain.ROUTE) {
					solutionAvailable = false;
					break;
				}
			}
			break;

		case OUEST:
			if (positionDepart.getX() - length <= ROAD_SIZE) {
				solutionAvailable = false;
				break;
			}
			log.debug("length of the way = {}", length);
			for (int i = 0; i < length; i++) {
				positionDepart.setX(positionDepart.getX() - 1);
				positionDepart2.setX(positionDepart2.getX() - 1);
				if (carte.get(positionDepart.getX()).get(positionDepart.getY()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getX()).get(positionDepart2.getY()).getTerrain() == Terrain.ROUTE) {
					solutionAvailable = false;
					break;
				}
			}
			break;

		case SUD:
			if (mapSize - positionDepart.getY() - length <= ROAD_SIZE) {
				solutionAvailable = false;
				break;
			}
			log.debug("length of the way = {}", length);
			for (int i = 0; i < length; i++) {
				positionDepart.setY(positionDepart.getY() + 1);
				positionDepart2.setY(positionDepart2.getY() + 1);
				if (carte.get(positionDepart.getX()).get(positionDepart.getY()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getX()).get(positionDepart2.getY()).getTerrain() == Terrain.ROUTE) {
					solutionAvailable = false;
					break;
				}
			}
			break;

		case EST:
			if (mapSize - positionDepart.getX() - length < ROAD_SIZE) {
				solutionAvailable = false;
				break;
			}
			log.debug("length of the way = {}", length);
			for (int i = 0; i < length; i++) {
				positionDepart.setX(positionDepart.getX() + 1);
				positionDepart2.setX(positionDepart2.getX() + 1);
				if (carte.get(positionDepart.getX()).get(positionDepart.getY()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getX()).get(positionDepart2.getY()).getTerrain() == Terrain.ROUTE) {
					solutionAvailable = false;
					log.debug(positionDepart.toString() + " " + positionDepart2.toString());
					break;
				}
			}
			break;
		}
		return solutionAvailable;
	}
	
	private boolean checkNewDirection(Orientation orientation, Direction direction, Position positionDepart, Position positionDepart2) {
		final int curveLength = ROAD_SIZE + 1;
		boolean solutionAvailable = true;
		switch (orientation) {
		case NORD:
			switch (direction) {
			case GAUCHE:
				if (positionDepart2.getX() > curveLength && positionDepart2.getY() > curveLength) {
					positionDepart2.setX(positionDepart2.getX() + 1);
					positionDepart.setX(positionDepart2.getX());
					positionDepart.setY(positionDepart2.getY() - 1);
					positionDepart2.setY(positionDepart.getY() - ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
							solutionAvailable = false;
							break;
						}
					}
				} else {
					solutionAvailable = false;
				}
				break;

			case DROITE:
				if (positionDepart2.getX() > ROAD_SIZE + 1 && mapSize - positionDepart2.getY() > ROAD_SIZE) {
					positionDepart2.setX(positionDepart.getX() + 1);
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart2.setY(positionDepart.getY() + 1);
					positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
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

		case OUEST:
			switch (direction) {
			case GAUCHE:
				if (positionDepart.getX() >= curveLength && mapSize - positionDepart.getY() >= curveLength) {
					positionDepart.setY(positionDepart2.getY() - 1);
					positionDepart2.setY(positionDepart2.getY() - 1);
					positionDepart.setX(positionDepart.getX() - 1);
					positionDepart2.setX(positionDepart2.getX() - ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;

			case DROITE:
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
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;
			}
			break;

		case SUD:
			switch (direction) {
			case GAUCHE:
				if (mapSize - positionDepart.getX() < ROAD_SIZE + 1 && mapSize - positionDepart.getY() < ROAD_SIZE + 1) {
					positionDepart.setX(positionDepart2.getX() - 1);
					positionDepart2.setX(positionDepart2.getX() - 1);
					positionDepart2.setY(positionDepart2.getY() + ROAD_SIZE);
					positionDepart.setY(positionDepart.getY() + 1);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;

			case DROITE:
				if (mapSize - positionDepart2.getX() > ROAD_SIZE && mapSize - positionDepart2.getY() > ROAD_SIZE) {
					positionDepart2.setX(positionDepart.getX() + 1);
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
					positionDepart2.setY(positionDepart2.getY() + 1);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;
			}
			break;

		case EST:
			switch (direction) {
			case GAUCHE:
				if (mapSize - positionDepart.getX() < ROAD_SIZE && mapSize - positionDepart.getY() < ROAD_SIZE) {
					positionDepart2.setY(positionDepart2.getY() + 1);
					positionDepart.setY(positionDepart2.getY());
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
							solutionAvailable = false;
							break;
						}
					}
				}
				break;

			case DROITE:
				if (positionDepart.getX() < ROAD_SIZE + 1 && positionDepart.getY() < ROAD_SIZE + 1) {
					positionDepart.setY(positionDepart.getY() - 1);
					positionDepart2.setY(positionDepart.getY());
					positionDepart.setX(positionDepart.getX() + 1);
					positionDepart2.setX(positionDepart2.getX() + ROAD_SIZE);
					for (int i = 0; i < curveLength; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getTerrain() == Terrain.ROUTE || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getTerrain() == Terrain.ROUTE) {
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

	private Orientation traceStartLine(Position positionDepart, Position positionDepart2) {
		log.debug("traceLigneDepart entering");
		int posDepart = getRandomNumber(1, mapSize - ROAD_SIZE);

		Orientation coteDepart = Orientation.values()[getRandomNumber(Orientation.values().length)];
		log.debug("start position depart : " + posDepart + ", direction : "	+ coteDepart);
		switch (coteDepart) {
		case NORD:
			positionDepart.setX(posDepart);
			positionDepart2.setX(posDepart + ROAD_SIZE - 1);
			positionDepart.setY(mapSize - 1);
			positionDepart2.setY(mapSize - 1);
			break;
		case EST:
			positionDepart.setY(posDepart);
			positionDepart2.setY(posDepart + ROAD_SIZE - 1);
			break;
		case SUD:
			positionDepart.setX(posDepart + ROAD_SIZE - 1);
			positionDepart2.setX(posDepart);
			break;
		case OUEST:
			positionDepart.setX(mapSize - 1);
			positionDepart2.setX(mapSize - 1);
			positionDepart.setY(posDepart + ROAD_SIZE - 1);
			positionDepart2.setY(posDepart);
			break;
		}
		log.debug("PositionDepart x=" + positionDepart.getX() + ", y=" + positionDepart.getY());
		log.debug("PositionDepart2 x=" + positionDepart2.getX() + ", y=" + positionDepart2.getY());
		traceLargeur(positionDepart, positionDepart2, Terrain.START_LINE);
		log.debug("traceLigneDepart exiting");
		return coteDepart;
	}
	
	private void traceEndLine(Orientation coteDepart, Position positionDepart, Position positionDepart2) {
		log.debug("Trace end line {}, {}", positionDepart.toString(), positionDepart2.toString());
		switch (coteDepart) {
		case NORD:
			positionDepart.setY(positionDepart.getY() - 1);
			positionDepart2.setY(positionDepart2.getY() - 1);
			break;
		case EST:
			positionDepart.setX(positionDepart.getX() + 1);
			positionDepart2.setX(positionDepart2.getX() + 1);
			break;
		case SUD:
			positionDepart.setY(positionDepart.getY() + 1);
			positionDepart2.setY(positionDepart2.getY() + 1);
			break;
		case OUEST:
			positionDepart.setX(positionDepart.getX() - 1);
			positionDepart2.setX(positionDepart2.getX() - 1);
			break;
		}
		endLinePositionList.add(positionDepart);
		endLinePositionList.add(positionDepart2);
		traceLargeur(positionDepart, positionDepart2, Terrain.END_LINE);
	}

	private void traceLargeur(Position positionDepart, Position positionDepart2) {
		traceLargeur(positionDepart, positionDepart2, Terrain.ROUTE);
	}

	private void traceLargeur(Position positionDepart,Position positionDepart2, Terrain terrain) {
		log.debug("traceLargeur begin");
		log.debug("Start Position1 [x = {}, y = {}]", positionDepart.getX(), positionDepart.getY());
		log.debug("Start Position2 [x = {}, y = {}]", positionDepart2.getX(), positionDepart2.getY());
		int minX = Math.min(positionDepart.getX(), positionDepart2.getX());
		int maxX = Math.max(positionDepart.getX(), positionDepart2.getX());
		int minY = Math.min(positionDepart.getY(), positionDepart2.getY());
		int maxY = Math.max(positionDepart.getY(), positionDepart2.getY());
		if (positionDepart.getX() == positionDepart2.getX()) {
			for (int i = minY; i <= maxY; i++) {
				carte.get(i).get(positionDepart.getX()).setTerrain(terrain);
			}
		}

		if (positionDepart.getY() == positionDepart2.getY()) {
			for (int i = minX; i <= maxX; i++) {
				try {
				carte.get(positionDepart.getY()).get(i).setTerrain(terrain);
				} catch (NullPointerException e) {
					display();
					e.printStackTrace();
				}
			}
		}
		log.debug("traceLargeur exiting");
	}

	public void display() {
		log.debug("display begin");
		for (int i = 0; i < mapSize; i++) {
			StringBuilder str = new StringBuilder();
			for (Case c : carte.get(i)) {
				if (c.getTerrain() == Terrain.HERBE) {
					str.append(0);
				} else {
					str.append(1);
				}
			}
			log.debug(str.toString());
		}
		log.debug("display exiting");
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

	public Case getCase(int h, int w) {
		if (h < 0 || h >= carte.size() || w < 0 || w >= carte.get(h).size()) {
			return null;
		}
		return carte.get(h).get(w);
	}

	public List<Position> getStartPosition() {
		return startPositionList;
	}
	
	public List<Position> getEndLinePosition() {
		return endLinePositionList;
	}
}
