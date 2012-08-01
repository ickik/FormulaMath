package fr.ickik.formulamath.model.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.DetailledRoadDirectionList;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.entity.RoadDirectionList;
import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.JCaseSide;

/**
 * Contains and handles the map of the application. The map is a everytime a square.<br>
 * This class creates and generates a random map. The map is at first time
 * initialized with full grass field and then it adds the road and next the design.
 * <br><br>
 * <b>Warning : </b>Correction of random generator, it starts at 0 but creation is not available and
 * finish the construction. A part of generated maps contains not completed road.
 * @author Ickik.
 * @version 0.1.016, 19 July 2012.
 */
public final class MapManager {

	public static final int ROAD_SIZE = 4;
	public static final int EMPTY_PLAYER = 0;
	private static final Logger log = LoggerFactory.getLogger(MapManager.class);
	private List<List<CaseModel>> carte;
	private int mapSize;
	private final List<Position> startingPositionList = new ArrayList<Position>(ROAD_SIZE);
	private final List<Position> startingPositionListSave = new ArrayList<Position>(ROAD_SIZE);
	private final List<Position> finishingLinePositionList = new ArrayList<Position>(2);
	private final RoadDirectionList roadList = new RoadDirectionList();
	private final DetailledRoadDirectionList detailledRoadList = new DetailledRoadDirectionList();

	/**
	 * Constructor of the map manager. It needs the size of the map.
	 * The size of the map is the number of case on one side of the square.
	 * @param size the number of cases of square's side.
	 */
	public MapManager() {
		log.trace("Constructor MapManager");
	}

	/**
	 * Initializes the map with the size given in argument and with default values.
	 * The map is every time a square so the size defines the dimension of all sides.
	 * @param size the size of the map.
	 */
	public void init(int size) {
		log.debug("Init begin");
		log.debug("Size of the map : {}", size);
		this.mapSize = size;
		carte = new ArrayList<List<CaseModel>>(size);
		for (int i = 0; i < mapSize; i++) {
			List<CaseModel> list = new ArrayList<CaseModel>(mapSize);
			for (int j = 0; j < mapSize; j++) {
				list.add(new CaseModel(getRandomField()));
			}
			carte.add(list);
		}
		landscapeInitialization();
		log.debug("Map initialized");
	}
	
	private Field getRandomField() {
		int value = getRandomNumber(20);
		switch (value % 10) {
		case 0:
			return Field.SAND;
		//case 1:
		//	return Field.WATER;
		default:
			return Field.GRASS;
		}
	}
	
	private void landscapeInitialization() {
		int nb = getRandomNumber(mapSize / 25);
		for (int i = 0; i < nb; i++) {
			int x = getRandomNumber(mapSize);
			int y = getRandomNumber(mapSize);
			CaseModel model = carte.get(y).get(x);
			Field field = getRandomField();
			field = Field.WATER;
			model.setField(field);
			partLandscapeInitialization(x, y, field, getRandomNumber(2, 10), getRandomNumber(2, 10));
		}
	}
	
	private void partLandscapeInitialization(int x, int y, Field field, int width, int height) {
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				if (isModelAvailable(i, j)) {
					if (getRandomNumber(3) == 1) {
						CaseModel model = carte.get(j).get(i);
						model.setField(field);
					}
				}
			}
		}
	}
	
	private boolean isModelAvailable(int x, int y) {
		if (x >= 0 && x < mapSize) {
			if (y >= 0 && y < mapSize) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Reinitializes the map manager to replay the same map.
	 */
	public void reinitialization() {
		log.debug("Reinitialization of map");
		startingPositionList.clear();
		startingPositionList.addAll(startingPositionListSave);
		for (List<CaseModel> modelList : carte) {
			for (CaseModel model : modelList) {
				model.setIdPlayer(EMPTY_PLAYER);
			}
		}
		log.debug("End of Map Reinitialization");
	}
	
	/**
	 * Reinitializes of variable to play again.
	 */
	public void fullReinitialization() {
		log.debug("FullReinitialization of map");
		startingPositionList.clear();
		finishingLinePositionList.clear();
		roadList.clear();
		log.debug("End of Map Reinitialization");
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
				startingPositionListSave.add(new Position(p.getX(), i));
			}
		} else if (p.getY() == p2.getY()) {
			for (int i = minX; i <= maxX; i++) {
				log.debug("{}", (new Position(i, p.getY()).toString()));
				startingPositionList.add(new Position(i, p.getY()));
				startingPositionListSave.add(new Position(i, p.getY()));
			}
		}
	}
	
	private int count(Queue<Direction> previousDirection, Direction direction) {
		int cpt = 0;
		for (Direction d : previousDirection) {
			if (direction == d) {
				cpt++;
			}
		}
		return cpt;
	}

	/**
	 * Creates the road on the map. The road has a start line and finish at the end line.
	 * A verification of the road is done on every direction change.
	 */
	public void constructRoad() {
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
		detailledRoadList.add(new RoadDirectionInformation(coteDepart, position, position.clone()));

		Queue<Direction> previousDirection = new ArrayDeque<Direction>(2);
		while (!isFinished) {
			Direction direction = Direction.values()[getRandomNumber(0, nbDirection)];
			if (previousDirection.size() == 2) {
				if (count(previousDirection, direction) == 2) {
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
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart2, JCaseSide.TOP);
						}
						traceBorderSide(positionDepart, JCaseSide.BOTTOM);
						getCase(positionDepart2.getY(), positionDepart2.getX() + ROAD_SIZE).setBorderCaseSide(JCaseSide.TOP_RIGHT_CORNER_ACUTE);
						getCase(positionDepart.getY(), positionDepart.getX() + 1).setBorderCaseSide(JCaseSide.BOTTOM_LEFT_CORNER_REFLEX);

						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
						log.debug("{} => {} exiting", coteDepart.name(),direction.name());
						coteDepart = Orientation.WEST;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(positionDepart.getY());
					if (len > mapSize / 2) {
						len = getRandomNumber(mapSize / 2);
					}
					
					log.debug("Length : {}", len);
					if (positionDepart.getY() - len < ROAD_SIZE) {
						len = positionDepart.getY() - 1;
						isFinished = true;
					}
					log.debug("Length after calculating : {}", len);
					if (!checkDirection(coteDepart, len, positionDepart.clone(), positionDepart2.clone())) {
						break;
					}
					Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					log.debug("length of the way = {}", len);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						traceRoadLine(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
					detailledRoadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
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
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart, JCaseSide.TOP);
						}
						traceBorderSide(positionDepart2, JCaseSide.BOTTOM);
						getCase(positionDepart.getY(), positionDepart.getX() - ROAD_SIZE).setBorderCaseSide(JCaseSide.TOP_LEFT_CORNER_ACUTE);
						getCase(positionDepart.getY() + ROAD_SIZE, positionDepart.getX()).setBorderCaseSide(JCaseSide.BOTTOM_RIGHT_CORNER_REFLEX);

						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
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
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart2, JCaseSide.LEFT);
						}
						traceBorderSide(positionDepart, JCaseSide.RIGHT);
						getCase(positionDepart2.getY() - ROAD_SIZE, positionDepart2.getX()).setBorderCaseSide(JCaseSide.TOP_LEFT_CORNER_ACUTE);
						getCase(positionDepart.getY() - 1, positionDepart.getX()).setBorderCaseSide(JCaseSide.BOTTOM_RIGHT_CORNER_REFLEX);

						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() - 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.SOUTH;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(positionDepart.getX());
					if (len > mapSize / 2) {
						len = getRandomNumber(mapSize / 2);
					}
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
						traceRoadLine(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
					detailledRoadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
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
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart, JCaseSide.LEFT);
						}
						traceBorderSide(positionDepart2, JCaseSide.RIGHT);
						getCase(positionDepart.getY() + ROAD_SIZE, positionDepart.getX()).setBorderCaseSide(JCaseSide.BOTTOM_LEFT_CORNER_ACUTE);
						getCase(positionDepart2.getY() + 1, positionDepart2.getX()).setBorderCaseSide(JCaseSide.TOP_RIGHT_CORNER_REFLEX);

						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() + 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
						coteDepart = Orientation.NORTH;
					}
					break;
				}
				break;

			case SOUTH:
				switch (direction) {
				case LEFT:
					if (mapSize - positionDepart.getX() < curveLength && mapSize - positionDepart.getY() < curveLength) {
						if (!checkNewDirection(Orientation.SOUTH, Direction.LEFT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						
						positionDepart.setX(positionDepart2.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						positionDepart2.setY(positionDepart2.getY() + ROAD_SIZE);
						positionDepart.setY(positionDepart.getY() + 1);
						Position centerDepart = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart2, JCaseSide.BOTTOM);
						}
						traceBorderSide(positionDepart, JCaseSide.TOP);
						getCase(positionDepart.getY(), positionDepart.getX() - ROAD_SIZE).setBorderCaseSide(JCaseSide.TOP_LEFT_CORNER_ACUTE);
						getCase(positionDepart.getY() + ROAD_SIZE, positionDepart.getX()).setBorderCaseSide(JCaseSide.BOTTOM_RIGHT_CORNER_REFLEX);

						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
						log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
						coteDepart = Orientation.EAST;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getY());
					if (len > mapSize / 2) {
						len = getRandomNumber(mapSize / 2);
					}
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
						traceRoadLine(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
					detailledRoadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
					log.debug(coteDepart.name() + " => " + direction.name() + " exiting");
					break;

				case RIGHT:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					if (positionDepart2.getX() > curveLength && mapSize - positionDepart2.getY() > curveLength) {
						if (!checkNewDirection(Orientation.SOUTH, Direction.RIGHT, positionDepart.clone(), positionDepart2.clone())) {
							break;
						}
						log.debug("{} => {} entering", coteDepart.name(), direction.name());
						
						positionDepart2.setX(positionDepart.getX() + 1);
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
						positionDepart2.setY(positionDepart2.getY() + 1);
						centerDepart = new Position((positionDepart.getX() + positionDepart2.getX() - 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart, JCaseSide.BOTTOM);
						}
						traceBorderSide(positionDepart2, JCaseSide.TOP);
						getCase(positionDepart.getY(), positionDepart.getX() + ROAD_SIZE).setBorderCaseSide(JCaseSide.BOTTOM_RIGHT_CORNER_ACUTE);
						getCase(positionDepart2.getY(), positionDepart2.getX() + 1).setBorderCaseSide(JCaseSide.TOP_LEFT_CORNER_REFLEX);

						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX() + 1) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.WEST, centerDepart, centerEnd));
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
						
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart2, JCaseSide.RIGHT);
						}
						traceBorderSide(positionDepart, JCaseSide.LEFT);
						getCase(positionDepart2.getY() + ROAD_SIZE, positionDepart2.getX()).setBorderCaseSide(JCaseSide.BOTTOM_RIGHT_CORNER_ACUTE);
						getCase(positionDepart2.getY(), positionDepart2.getX()  - ROAD_SIZE).setBorderCaseSide(JCaseSide.TOP_LEFT_CORNER_REFLEX);
						
						
						Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() + 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.NORTH, centerDepart, centerEnd));
						log.debug("{} => {} exiting", coteDepart.name(), direction.name());
						coteDepart = Orientation.NORTH;
					}
					break;

				case MIDDLE:
					log.debug("{} => {} entering", coteDepart.name(), direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getX() - 1);
					log.trace("Random length with max length {} : {}", mapSize - positionDepart.getX() - 1, len);
					if (len > mapSize / 2) {
						len = getRandomNumber(mapSize / 2);
					}
					log.trace("calculated length : {}", len);
					if (mapSize - positionDepart.getX() - len - 1 < ROAD_SIZE) {
						len = mapSize - positionDepart.getX() - 2;
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
						traceRoadLine(positionDepart, positionDepart2);
					}
					Position centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY()) / 2);
					roadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
					detailledRoadList.add(new RoadDirectionInformation(Orientation.EAST, centerDepart, centerEnd));
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
						traceCurveBorderSide(positionDepart, positionDepart2, coteDepart, direction);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceCurveLargeur(positionDepart, positionDepart2);
							traceBorderSide(positionDepart, JCaseSide.RIGHT);
						}
						traceBorderSide(positionDepart2, JCaseSide.LEFT);
						getCase(positionDepart2.getY() - ROAD_SIZE, positionDepart2.getX()).setBorderCaseSide(JCaseSide.TOP_RIGHT_CORNER_ACUTE);
						getCase(positionDepart2.getY(), positionDepart2.getX() - ROAD_SIZE).setBorderCaseSide(JCaseSide.BOTTOM_LEFT_CORNER_REFLEX);

						centerEnd = new Position((positionDepart.getX() + positionDepart2.getX()) / 2, (positionDepart.getY() + positionDepart2.getY() - 1) / 2);
						roadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
						detailledRoadList.add(new RoadDirectionInformation(Orientation.SOUTH, centerDepart, centerEnd));
						log.debug("{} => {} exiting", coteDepart.name(), direction.name());
						coteDepart = Orientation.SOUTH;
					}
					break;
				}
			}
		}
		traceFinishingLine(coteDepart, positionDepart, positionDepart2);
		updateLastDirectionRoad();
		log.debug("constructRoad end");
		log.debug("Road list operational : size {}",roadList.size());
		log.debug("{}", roadList.toString());
	}
	
	private void updateLastDirectionRoad() {
		switch(roadList.getLast().getOrientation()) {
		case NORTH :
			roadList.getLast().getEnd().setY(0);
			detailledRoadList.getLast().getEnd().setY(0);
			break;
		case WEST :
			roadList.getLast().getEnd().setX(0);
			detailledRoadList.getLast().getEnd().setX(0);
			break;
		case SOUTH :
			roadList.getLast().getEnd().setY(mapSize - 1);
			detailledRoadList.getLast().getEnd().setY(mapSize - 1);
			break;
		case EAST :
			roadList.getLast().getEnd().setX(mapSize - 1);
			detailledRoadList.getLast().getEnd().setX(mapSize - 1);
			break;
		}
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
			if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
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
			if (carte.get(positionDepart.getY()).get(positionDepart.getX()).getField() == Field.ROAD || carte.get(positionDepart2.getY()).get(positionDepart2.getX()).getField() == Field.ROAD) {
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
			default:
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
			default:
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
			default:
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
			default:
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
		traceLargeur(positionDepart, positionDepart2, Field.STARTING_LINE, false);
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
		traceLargeur(positionDepart, positionDepart2, Field.FINISHING_LINE, false);
	}

	private void traceCurveLargeur(Position positionDepart, Position positionDepart2) {
		traceLargeur(positionDepart, positionDepart2, Field.ROAD, false);
	}
	
	private void traceRoadLine(Position positionDepart, Position positionDepart2) {
		traceLargeur(positionDepart, positionDepart2, Field.ROAD, true);
	}
	

	private void traceLargeur(Position positionDepart,Position positionDepart2, Field terrain, boolean isBorder) {
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
				if (isBorder) {
					if (i == minY) {
						carte.get(i).get(positionDepart.getX()).setBorderCaseSide(JCaseSide.TOP);
					} else if (i == maxY) {
						carte.get(i).get(positionDepart.getX()).setBorderCaseSide(JCaseSide.BOTTOM);
					}
				}
			}
		}

		if (positionDepart.getY() == positionDepart2.getY()) {
			for (int i = minX; i <= maxX; i++) {
				carte.get(positionDepart.getY()).get(i).setField(terrain);
				if (isBorder) {
					if (i == minX) {
						carte.get(positionDepart.getY()).get(i).setBorderCaseSide(JCaseSide.LEFT);
					} else if (i == maxX) {
						carte.get(positionDepart.getY()).get(i).setBorderCaseSide(JCaseSide.RIGHT);
					}
				}
			}
		}
		log.debug("traceLargeur exiting");
	}
	
	private void traceBorderSide(Position position, JCaseSide caseSide) {
		getCase(position.getY(), position.getX()).setBorderCaseSide(caseSide);
	}
	
	private void traceCurveBorderSide(Position positionDepart,Position positionDepart2, Orientation orientation, Direction direction) {
		log.debug("reinitBorderSide begin");
		int length = ROAD_SIZE - 1;
		switch (orientation) {
		case NORTH :
			switch (direction) {
			case LEFT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart.getY() - i, positionDepart.getX() - 1).setBorderCaseSide(JCaseSide.RIGHT);
				}
				break;
			case RIGHT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart2.getY() - i, positionDepart2.getX() - 1).setBorderCaseSide(JCaseSide.LEFT);
				}
				break;
			default:
				break;
			}
			break;
		case WEST:
			switch (direction) {
			case LEFT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart.getY() + 1, positionDepart.getX() - i).setBorderCaseSide(JCaseSide.TOP);
				}
				break;
			case RIGHT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart2.getY() - 1, positionDepart2.getX() - i).setBorderCaseSide(JCaseSide.BOTTOM);
				}
				break;
			default:
				break;
			}
			break;
		case SOUTH:
			switch (direction) {
			case LEFT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart.getY() + i, positionDepart.getX() - 1).setBorderCaseSide(JCaseSide.LEFT);
				}
				break;
			case RIGHT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart2.getY() + i, positionDepart2.getX() - 1).setBorderCaseSide(JCaseSide.RIGHT);
				}
				break;
			default:
				break;
			}
			break;
		case EAST:
			switch (direction) {
			case LEFT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart.getY() - 1, positionDepart.getX() + i).setBorderCaseSide(JCaseSide.BOTTOM);
				}
				break;
			case RIGHT:
				for (int i = 0; i < length; i++) {
					getCase(positionDepart2.getY() - 1, positionDepart2.getX() + i).setBorderCaseSide(JCaseSide.TOP);
				}
				break;
			default:
				break;
			}
			break;
		}
		log.debug("traceLargeur exiting");
	}

	@Override
	public String toString() {
		StringBuilder display = new StringBuilder("\n");
		for (int i = 0; i < mapSize; i++) {
			StringBuilder str = new StringBuilder();
			for (CaseModel c : carte.get(i)) {
				str.append(c.getField().getValue());
			}
			display.append(str);
			display.append("\n");
		}
		return display.toString();
	}

	/**
	 * Return the size of the map.
	 * @return the size of the map.
	 */
	public int getMapSize() {
		return mapSize;
	}

	/**
	 * Return a random number in range min argument inclusive to max argument exclusive.<br>
	 * <b>min <= random number < max</b>
	 * @param min the minimum random number inclusive.
	 * @param max the maximum random number exclusive.
	 * @return a random number between min and max
	 */
	private int getRandomNumber(int min, int max) {
		Random r = new Random();
		return r.nextInt(max - min) + min;
	}

	/**
	 * Return a random number in range 1 to max argument exclusive.
	 * @param max the maximum exclusive.
	 * @return a random number.
	 */
	private int getRandomNumber(int max) {
		if (max == 0) {
			return 0;
		}
		return getRandomNumber(1, max);
	}

	/**
	 * Return the {@link CaseModel} located at (w,h) coordinates.
	 * @param h the y axis coordinate
	 * @param w the x axis coordinate
	 * @return the case model associates or null if the coordinates are out of the map.
	 */
	public CaseModel getCase(int h, int w) {
		if (h < 0 || h >= carte.size() || w < 0 || w >= carte.get(h).size()) {
			return null;
		}
		return carte.get(h).get(w);
	}
	
	/**
	 * Return a list of list (2 dimension) that represents all case of the map.
	 * Every case contains a model and the list is a super model of these.
	 * @return 2 dimension list which contains the model of the map.
	 */
	public List<List<CaseModel>> getMap() {
		return carte;
	}

	/**
	 * Return the list of positions on starting lane.
	 * @return the list of positions on starting lane.
	 */
	public List<Position> getStartingPositionList() {
		return startingPositionList;
	}
	
	/**
	 * Return the list of positions on finishing lane.
	 * @return the list of positions on finishing lane.
	 */
	public List<Position> getFinishingLinePositionList() {
		return finishingLinePositionList;
	}
	
	/**
	 * Return the list of position of ideal way to run the race.
	 * @return the list of position of ideal way to run the race.
	 */
	public List<RoadDirectionInformation> getRoadDirectionInformationList() {
		return roadList;
	}
	
	public List<RoadDirectionInformation> getDetailledRoadDirectionInformationList() {
		return detailledRoadList;
	}
	
	/**
	 * Merge the MapManager parameter with this MapManager.<br>
	 * It remove all data and replace them with parameter MapManager data.
	 * @param mapManager the MapManager to copy.
	 */
	public void mergeMapManager(MapManager mapManager) {
		this.mapSize = mapManager.getMapSize();
		this.carte.clear();
		this.carte.addAll(mapManager.getMap());
		this.detailledRoadList.clear();
		this.detailledRoadList.addAll(mapManager.getRoadDirectionInformationList());
		this.finishingLinePositionList.clear();
		this.finishingLinePositionList.addAll(mapManager.getFinishingLinePositionList());
		this.roadList.clear();
		this.roadList.addAll(mapManager.getRoadDirectionInformationList());
		this.startingPositionList.clear();
		this.startingPositionListSave.clear();
		this.startingPositionList.addAll(mapManager.getStartingPositionList());
		this.startingPositionListSave.addAll(mapManager.getStartingPositionList());
	}
}
