package fr.ickik.formulamath.model.ai;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.DetailledRoadDirectionInformation;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;
import fr.ickik.formulamath.view.JCase;
import fr.ickik.formulamath.view.MainFrame;

/**
 * Abstract class which defines common methods for Artificial Intelligence Classes.
 * @author Ickik
 * @version 0.1.004, 22th February 2013.
 * @since 0.3.9
 */
abstract class AbstractAILevel implements AILevel {

	private boolean isLastPlay = false;
	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AbstractAILevel.class);
	
	public AbstractAILevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	List<Vector> vectorListFiltered(Position position, List<Vector> list) {
		log.trace("vectorListFiltered({} , {})", position, list);
		int marge = MainFrame.MAP_MARGIN / 2;
		int xTrayPanel = getCoordinateLimit(position.getX() + marge, mapManager.getMapSize());
		int yTrayPanel = getCoordinateLimit(position.getY() + marge, mapManager.getMapSize());
		log.trace("Player position :{}", position.toString());
		log.trace("Player position on map : ( {}, {} )", xTrayPanel, yTrayPanel);
		for (int i = 0; i < list.size();) {
			Vector v = list.get(i);
			if (v.getX() == 0 && v.getY() == 0) {
				list.remove(i);
				continue;
			}
			JCase c = mapManager.getCarteComponent().get(yTrayPanel).get(xTrayPanel);
			int y = getCoordinateLimit(yTrayPanel - v.getY(), mapManager.getMapSize());
			int x = getCoordinateLimit(xTrayPanel + v.getX(), mapManager.getMapSize());
			log.debug("solution : {}", list.get(i).toString());
			log.trace("Player final  position on map : ( {}, {} )", x, y);

			final JCase c2 = mapManager.getCarteComponent().get(y).get(x);
			Line2D line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
			log.trace("Vector's line on map from ( {}, {} ) to ( {} , {} )", new Object[]{c.getX(), c.getY(), c2.getX(), c2.getY()});
			if (isGrassIntersection(line)) {
				log.trace("{} intersects grass", list.get(i).toString());
				list.remove(i);
			} else {
				i++;
			}
		}
		log.trace("end of vectorListFiltered : {}", list);
		return list;
	}
	
	private boolean isGrassIntersection(Line2D shape) {
		log.debug("isGrassIntersection");
		return mapManager.checkIntersection(shape, Field.GRASS);
	}
	
	private int getCoordinateLimit(int coordinate, int mapSize) {
		if (coordinate < MainFrame.MAP_MARGIN / 2) {
			return MainFrame.MAP_MARGIN / 2;
		} else if (coordinate >= mapSize + (MainFrame.MAP_MARGIN / 2)) {
			return mapSize + (MainFrame.MAP_MARGIN / 2) - 1;
		}
		return coordinate;
	}

	public boolean isLastPlay() {
		return isLastPlay;
	}
	
	@Override
	public void reinitIsLastPlay() {
		this.isLastPlay = false;
	}
	
	@Override
	public Position getStartingPosition() {
		List<Position> list = mapManager.getStartingPositionList();
		if (mapManager.getStartingPositionList().size() == 1) {
			return list.remove(0);
		}
		int index = list.size() / 2;
		return list.remove(index);
	}
	
	private Vector getVector(Orientation orientation, int value) {
		switch (orientation) {
		case NORTH:
			return new Vector(0, value);
		case WEST:
			return new Vector(-value, 0);
		case SOUTH:
			return new Vector(0, -value);
		case EAST:
			return new Vector(value, 0);
		}
		return null;
	}
	
	int getFirstMove(int distance) {
		log.debug("getFirstMove : distance = {}", Integer.toString(distance));
		HashMap<Integer,Integer> distanceMap = new HashMap<Integer,Integer>();
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
		int d = distance - vitesse;
		int newStep = step + 1;
		int nbLess = getNbStep(d, vitesse - 1, newStep);
		int nbEqual = getNbStep(d, vitesse, newStep);
		return nbLess <= nbEqual ? nbLess : nbEqual;
	}
	
	/**
	 * Return the number of step to run the distance depending the speed.
	 * This method is recursively called with a variation of speed from 1, 0 or -1.
	 * @param distance the distance to run
	 * @param vitesse the current speed
	 * @param step the current number of step
	 * @return the number of step to run the distance
	 */
	int getNbStep(int distance, int vitesse, int step) {
		if ((distance == 0 || distance == 1) && vitesse == 1) {
			return step;
		}
		if (distance < 0 || vitesse <= 0) {
			return Integer.MAX_VALUE;
		}
		int d = distance - vitesse;
		int newStep = step + 1;
		int nbLess = getNbStep(d, vitesse - 1, newStep);
		int nbEqual = getNbStep(d, vitesse, newStep);
		int nbMore = getNbStep(d, vitesse + 1, newStep);
		int prev = nbLess <= nbEqual ? nbLess : nbEqual;
		return nbMore <= prev ? nbMore : prev;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition) {
		log.debug("getFirstMove for {}", player.toString());
		DetailledRoadDirectionInformation info = mapManager.getDetailledRoadDirectionInformationList().get(0);
		
		Vector vector = null;

		if (mapManager.getDetailledRoadDirectionInformationList().size() == 1) {
			log.trace("The road is a right line");
			vector = getVector(info.getOrientation(), mapManager.getMapSize() - 1);
			playerRoadPosition.put(player.getId(), 0);
		} else {
			log.trace("Curve exists in the road");
			int len = info.getLength();
			if (info.getInitialOrientation() == info.getOrientation()) {
				int val = getFirstMove(len);
				log.trace("length of the road : {}, road :{}", len, info.toString());
				log.trace("length of the first move found : {}", val);
				vector = getVector(info.getOrientation(), val);
				if (len > 4) {
					playerRoadPosition.put(player.getId(), 0);
				} else {
					playerRoadPosition.put(player.getId(), 1);
				}
			} else {
				List<Position> list = mapManager.getStartingPositionListSave();
				int index = list.indexOf(player.getPosition());
				boolean isNextWayCurve = mapManager.getDetailledRoadDirectionInformationList().get(1).getInitialOrientation() != mapManager.getDetailledRoadDirectionInformationList().get(1).getOrientation();
				int lenNextWay = mapManager.getDetailledRoadDirectionInformationList().get(1).getLength();
				playerRoadPosition.put(player.getId(), 1);
				switch (info.getInitialOrientation()) {
				case NORTH:
					
					switch(info.getOrientation()) {
					case WEST:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-3, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-4, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-5, 1);
								}
							}
							break;
						}
						break;
					case EAST:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(4, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(3, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(2, 1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(4, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(3, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(2, 1);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(3, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(2, 2);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(2, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(1, 1);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(3, 2);
								}
							}
							break;
						}
						break;
					}
					
					break;
					
				case WEST:
					switch(info.getOrientation()) {
					case NORTH:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, 1);
								}
							}
							break;
						}
						break;
					case SOUTH:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						}
						break;
					}
					break;
					
				case SOUTH:
					
					switch(info.getOrientation()) {
					case WEST:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() - 2).isOccuped()) {
									return new Vector(-2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() - 1).isOccuped()) {
									return new Vector(-1, -1);
								}
							}
							break;
						}
						break;
					case EAST:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -1);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						}
						break;
					}
					break;
					
				case EAST:
					switch(info.getOrientation()) {
					case NORTH:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 3, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 3);
								} else if (!mapManager.getCase(player.getPosition().getY() - 3, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 3);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, 2);
								} else if (!mapManager.getCase(player.getPosition().getY() - 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, 1);
								}
							}
							break;
						}
						break;
					case SOUTH:
						switch(index) {
						case 0:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						case 1:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						case 2:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						case 3:
							if (isNextWayCurve || lenNextWay < 5) {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							} else {
								if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 2, player.getPosition().getX() + 2).isOccuped()) {
									return new Vector(2, -2);
								} else if (!mapManager.getCase(player.getPosition().getY() + 1, player.getPosition().getX() + 1).isOccuped()) {
									return new Vector(1, -1);
								}
							}
							break;
						}
						break;
					}
					break;
				}
			}
		}
		
		return vector;
	}
	
	int getHighDistance(int distance) {
		int sum = 0;
		int value = 1;
		while(sum + value <= distance) {
			sum += value;
			value++;
		}
		return value - 1;
	}
	
	boolean isMovingAvailable(int xMove, int yMove) {
		CaseModel model = mapManager.getCase(yMove, xMove);
		if (model != null && model.getField() == Field.GRASS) {
			return false;
		}
		return true;
	}
	
	boolean isMovingAvailable(int xMove, int yMove, Player player) {
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
	
	List<Vector> getVectorsSolution(Position position, Vector vector) {
		List<Vector> list = new ArrayList<Vector>(5);
		if (isMovingAvailable(position.getX() + vector.getX(), position.getY() + vector.getY())) {
			list.add(new Vector(vector.getX(), vector.getY()));
		}
		if (isMovingAvailable(position.getX() + vector.getX() - 1, position.getY() + vector.getY())) {
			list.add(new Vector(vector.getX() - 1, vector.getY()));
		}
		if (isMovingAvailable(position.getX() + vector.getX() + 1, position.getY() + vector.getY())) {
			list.add(new Vector(vector.getX() + 1, vector.getY()));
		}
		if (isMovingAvailable(position.getX() + vector.getX(), position.getY() + vector.getY() + 1)) {
			list.add(new Vector(vector.getX(), vector.getY() - 1));
		}
		if (isMovingAvailable(position.getX() + vector.getX(), position.getY() + vector.getY() - 1)) {
			list.add(new Vector(vector.getX(), vector.getY() + 1));
		}
		return vectorListFiltered(position, list);
	}
	
	List<Vector> getVectorsPossibilities(Player player) {
		log.trace("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);
		log.trace("Player {} , init vector {}", player.toString(), player.getVector().toString());
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(player.getVector());
			log.trace(player.getVector().toString());
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX() - 1, player.getPosition().getY() - player.getVector().getY(), player)) {
			Vector v1 = new Vector(player.getVector().getX() - 1, player.getVector().getY());
			list.add(v1);
			log.trace(v1.toString());
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX() + 1, player.getPosition().getY() - player.getVector().getY(), player)) {
			Vector v2 = new Vector(player.getVector().getX() + 1, player.getVector().getY());
			list.add(v2);
			log.trace(v2.toString());
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY() + 1, player)) {
			Vector v3 = new Vector(player.getVector().getX(), player.getVector().getY() - 1);
			list.add(v3);
			log.trace(v3.toString());
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY() - 1, player)) {
			Vector v4 = new Vector(player.getVector().getX(), player.getVector().getY() + 1);
			list.add(v4);
			log.trace(v4.toString());
		}
		log.debug("number of vectors possible : {}", list.size());
		log.trace("getVectorsPossibilities filtering exiting");
		return vectorListFiltered(player.getPosition(), list);
	}
	
	/**
	 * Return the adding length of the vector to run as fastest as possible the straight line.
	 * @param distance the total distance to run
	 * @param vitesse the current speed.
	 * @return the number to add to the current vector.
	 */
	int getNextPlay(int distance, int vitesse) {
		log.trace("getNextPlay({}, {})", distance, vitesse);
		int v = vitesse < 0 ? -vitesse : vitesse;
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
}
