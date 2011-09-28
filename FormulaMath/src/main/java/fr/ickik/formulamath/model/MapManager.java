package fr.ickik.formulamath.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.Case;
import fr.ickik.formulamath.Direction;
import fr.ickik.formulamath.Orientation;
import fr.ickik.formulamath.Position;
import fr.ickik.formulamath.Terrain;

public class MapManager {

	private final List<List<Case>> carte;
	private final int mapSize;
	private final int ROAD_SIZE = 4;
	private final List<Position> startPositionList = new ArrayList<Position>(2);

	private static final Logger log = LoggerFactory.getLogger(MapManager.class);

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
		final int curveLength = 9;
		boolean isFinished = false;
		int nbDirection = Direction.values().length;

		log.debug("constructRoad begin");
		Position positionDepart = new Position();
		Position positionDepart2 = new Position();
		Orientation coteDepart = traceLigneDepart(positionDepart,
				positionDepart2);

		initStartPosition(positionDepart, positionDepart2);

		while (!isFinished) {
			Direction direction = Direction.values()[getRandomNumber(nbDirection)];

			switch (coteDepart) {
			case NORD:
				switch (direction) {
				case GAUCHE:
					if (mapSize - positionDepart2.getX() > ROAD_SIZE
							&& mapSize - positionDepart2.getY() > ROAD_SIZE) {
						log.debug("{} => {} entering", coteDepart.name(),
								direction.name());
						positionDepart.setX(positionDepart2.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						positionDepart.setY(positionDepart2.getY() + 1);
						positionDepart2.setY(positionDepart.getY() + ROAD_SIZE);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug("{} => {} exiting", coteDepart.name(),
								direction.name());
						coteDepart = Orientation.EST;
					}
					break;

				case MILIEU:
					log.debug("{} => {} entering", coteDepart.name(),
							direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getY());
					if (mapSize - positionDepart.getY() - len < ROAD_SIZE) {
						len = mapSize - positionDepart.getY() - 1;
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() + 1);
						positionDepart2.setY(positionDepart2.getY() + 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug("{} => {} exiting", coteDepart.name(),
							direction.name());
					break;

				case DROITE:
					if (positionDepart2.getX() > ROAD_SIZE + 1
							&& mapSize - positionDepart2.getY() > ROAD_SIZE) {
						log.debug(coteDepart.name() + " => " + direction.name()
								+ " entering");
						positionDepart2.setX(positionDepart.getX());
						positionDepart2.setY(positionDepart.getY() + 1);
						positionDepart.setY(positionDepart.getY() + ROAD_SIZE);
						for (int i = 0; i < curveLength; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name()
								+ " exiting");
						coteDepart = Orientation.OUEST;
					}
					break;
				}
				break;

			case OUEST:
				switch (direction) {
				case GAUCHE:
					log.debug(coteDepart.name() + " => " + direction.name()
							+ " entering");
					if (mapSize - positionDepart.getX() > ROAD_SIZE + 1
							&& positionDepart.getY() > ROAD_SIZE + 1) {
						log.debug(coteDepart.name() + " => " + direction.name());
						positionDepart.setX(positionDepart2.getX() + 1);
						positionDepart.setY(positionDepart2.getY());
						positionDepart2.setX(positionDepart.getX() + ROAD_SIZE
								- 1);
						for (int i = 0; i < 9; i++) {
							if (i != 0) {
								positionDepart.setY(positionDepart.getY() - 1);
								positionDepart2
										.setY(positionDepart2.getY() - 1);
							}
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name()
								+ " exiting");
						coteDepart = Orientation.NORD;
					}
					break;

				case MILIEU:
					log.debug("{} => {} entering", coteDepart.name(),
							direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getX());
					if (mapSize - len <= ROAD_SIZE) {
						len = mapSize - positionDepart.getY() - 1;
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					if (len == 0) {
						isFinished = true;
						break;
					}
					for (int i = 0; i < len; i++) {
						positionDepart.setX(positionDepart.getX() + 1);
						positionDepart2.setX(positionDepart2.getX() + 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug(coteDepart.name() + " => " + direction.name()
							+ " ended");
					break;

				case DROITE:
					log.debug("{} => {} entering", coteDepart.name(),
							direction.name());
					if (mapSize - positionDepart.getX() <= ROAD_SIZE
							&& mapSize - positionDepart.getY() <= ROAD_SIZE) {
						log.debug(coteDepart.name() + " => " + direction.name());
						positionDepart2.setX(positionDepart.getX() + 1);
						positionDepart2.setY(positionDepart.getY());
						positionDepart.setX(positionDepart.getX() + ROAD_SIZE);

						log.debug("Start Position1 [x = {}, y = {}]",
								positionDepart.getX(), positionDepart.getY());
						log.debug("Start Position2 [x = {}, y = {}]",
								positionDepart2.getX(), positionDepart2.getY());
						for (int i = 0; i < 9; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						coteDepart = Orientation.SUD;
					}
					break;
				}
				break;

			case SUD:
				switch (direction) {
				case GAUCHE:
					if (positionDepart.getX() < ROAD_SIZE + 1
							&& positionDepart.getY() < ROAD_SIZE + 1) {
						log.debug("{} => {} entering", coteDepart.name(),
								direction.name());
						positionDepart.setX(positionDepart2.getX());
						positionDepart.setY(positionDepart2.getY());
						positionDepart2.setX(positionDepart.getX() - ROAD_SIZE);
						for (int i = 0; i < 9; i++) {
							positionDepart.setX(positionDepart.getX() - 1);
							positionDepart2.setX(positionDepart2.getX() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name()
								+ " exiting");
						coteDepart = Orientation.EST;
					}
					break;

				case MILIEU:
					log.debug("{} => {} entering", coteDepart.name(),
							direction.name());
					int len = getRandomNumber(positionDepart.getY());
					if (positionDepart.getY() - len <= ROAD_SIZE) {
						len = positionDepart.getY();
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					for (int i = 0; i < len; i++) {
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setY(positionDepart2.getY() - 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug(coteDepart.name() + " => " + direction.name()
							+ " exiting");
					break;

				case DROITE:
					if (mapSize - positionDepart2.getX() > ROAD_SIZE
							&& mapSize - positionDepart2.getY() > ROAD_SIZE) {
						log.debug("{} => {} entering", coteDepart.name(),
								direction.name());
						positionDepart.setY(positionDepart.getY() - 1);
						positionDepart2.setX(positionDepart.getX());
						positionDepart2
								.setY(positionDepart2.getY() - ROAD_SIZE);
						for (int i = 0; i < 9; i++) {
							positionDepart.setX(positionDepart.getX() + 1);
							positionDepart2.setX(positionDepart2.getX() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug(coteDepart.name() + " => " + direction.name()
								+ " exiting");
						coteDepart = Orientation.OUEST;
					}
					break;
				}
				break;

			case EST:
				switch (direction) {
				case GAUCHE:
					if (positionDepart.getX() < mapSize - ROAD_SIZE - 1
							&& positionDepart.getY() < ROAD_SIZE + 1) {
						log.debug("{} => {} entering", coteDepart.name(),
								direction.name());
						positionDepart2.setX(positionDepart.getX());
						positionDepart2.setY(positionDepart.getY());
						positionDepart2.setX(positionDepart.getX() - ROAD_SIZE);
						for (int i = 0; i < 9; i++) {
							positionDepart.setY(positionDepart.getY() + 1);
							positionDepart2.setY(positionDepart2.getY() + 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug("{} => {} exiting", coteDepart.name(),
								direction.name());
						coteDepart = Orientation.SUD;
					}
					break;

				case MILIEU:
					log.debug("{} => {} entering", coteDepart.name(),
							direction.name());
					int len = getRandomNumber(mapSize - positionDepart.getX());
					if (positionDepart.getX() - len < ROAD_SIZE) {
						len = positionDepart.getX() - 1;
						isFinished = true;
					}
					log.debug("length of the way = {}", len);
					if (len == 0) {
						isFinished = true;
						break;
					}
					for (int i = 0; i < len; i++) {
						positionDepart.setX(positionDepart.getX() - 1);
						positionDepart2.setX(positionDepart2.getX() - 1);
						traceLargeur(positionDepart, positionDepart2);
					}
					log.debug("{} => {} exiting", coteDepart.name(),
							direction.name());
					break;

				case DROITE:
					if (positionDepart.getX() < ROAD_SIZE + 1
							&& positionDepart.getY() < ROAD_SIZE + 1) {
						log.debug("{} => {} entering", coteDepart.name(),
								direction.name());
						positionDepart2.setX(positionDepart.getX());
						positionDepart2.setY(positionDepart.getY());
						positionDepart2.setX(positionDepart.getX() - ROAD_SIZE);
						for (int i = 0; i < 9; i++) {
							positionDepart.setY(positionDepart.getY() - 1);
							positionDepart2.setY(positionDepart2.getY() - 1);
							traceLargeur(positionDepart, positionDepart2);
						}
						log.debug("{} => {} exiting", coteDepart.name(),
								direction.name());
						coteDepart = Orientation.NORD;
					}
					break;
				}
			}
		}
		log.debug("constructRoad end");
	}

	private Orientation traceLigneDepart(Position positionDepart,
			Position positionDepart2) {
		log.debug("traceLigneDepart entering");
		int posDepart = getRandomNumber(1, mapSize - ROAD_SIZE);

		Orientation coteDepart = Orientation.values()[getRandomNumber(Orientation
				.values().length)];
		log.debug("start position depart : " + posDepart + ", direction : "
				+ coteDepart);
		switch (coteDepart) {
		case NORD:
			positionDepart.setX(posDepart + ROAD_SIZE - 1);
			positionDepart2.setX(posDepart);
			break;
		case EST:
			positionDepart.setX(mapSize - 1);
			positionDepart2.setX(mapSize - 1);
			positionDepart.setY(posDepart + ROAD_SIZE - 1);
			positionDepart2.setY(posDepart);
			break;
		case SUD:
			positionDepart.setX(posDepart);
			positionDepart2.setX(posDepart + ROAD_SIZE - 1);
			positionDepart.setY(mapSize - 1);
			positionDepart2.setY(mapSize - 1);
			break;
		case OUEST:
			positionDepart.setY(posDepart);
			positionDepart2.setY(posDepart + ROAD_SIZE - 1);
			break;
		}
		log.debug("PositionDepart x=" + positionDepart.getX() + ", y="
				+ positionDepart.getY());
		log.debug("PositionDepart2 x=" + positionDepart2.getX() + ", y="
				+ positionDepart2.getY());
		traceLargeur(positionDepart, positionDepart2, Terrain.START_LINE);
		log.debug("traceLigneDepart exiting");
		return coteDepart;
	}

	private void traceLargeur(Position positionDepart, Position positionDepart2) {
		traceLargeur(positionDepart, positionDepart2, Terrain.ROUTE);
	}

	private void traceLargeur(Position positionDepart,
			Position positionDepart2, Terrain terrain) {
		log.debug("traceLargeur begin");
		log.debug("Start Position1 [x = {}, y = {}]", positionDepart.getX(),
				positionDepart.getY());
		log.debug("Start Position2 [x = {}, y = {}]", positionDepart2.getX(),
				positionDepart2.getY());
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
				carte.get(positionDepart.getY()).get(i).setTerrain(terrain);
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
			System.out.println(str);
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
		Random r = new Random();
		return r.nextInt(max);
	}

	public Case getCase(int h, int w) {
		return carte.get(h).get(w);
	}

	public List<Position> getStartPosition() {
		return startPositionList;
	}
}
