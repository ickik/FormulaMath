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
 * Class implements a medium level computer intelligence.
 * @author Ickik
 * @version 0.1.001, 14 August 2012
 * @since 0.3.9
 */
public final class AIMediumLevel extends AbstractAILevel {


	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AIMediumLevel.class);
	private boolean isLastPlay = false;

	public AIMediumLevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	@Override
	public Vector getNextPlay(Player player,  Map<Integer, Integer> playerRoadPosition) {
		int roadPosition = playerRoadPosition.get(player.getId());
		DetailledRoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(roadPosition);
		
		int len = r.getLengthToEnd(player.getPosition()) - 1;
		Vector vector = null;
		log.debug("AI rest length of the vector:{}", len);
		log.trace("Orientation: {}", r.getOrientation());
		List<Vector> solutionList = getVectorsPossibilities(player);
		if (solutionList.isEmpty()) {
			return null;
		}
		boolean isLastDirection = mapManager.getDetailledRoadDirectionInformationList().size() == roadPosition + 1;
		if (len > 3 || isLastDirection) {
			switch (r.getOrientation()) {
			case NORTH:
				int d = getNextPlay(len, player.getVector().getY());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(0, player.getVector().getY() + d);
				break;
			case SOUTH:
				d = getNextPlay(len, player.getVector().getY());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(0, player.getVector().getY() - d);
				break;
			case WEST:
				d = getNextPlay(len, player.getVector().getX());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() - d, 0);
				break;
			case EAST:
				d = getNextPlay(len, player.getVector().getX());
				if (isLastDirection) {
					d = 1;
				}
				log.debug("Next play : {}", d);
				vector = new Vector(player.getVector().getX() + d, 0);
				break;
			}
			if (solutionList.contains(vector)) {
				return vector;
			}
			Vector tmpVector = null;
			for (Vector v : solutionList) {
				switch (r.getOrientation()) {
				case NORTH:
					if ((v.getY() == vector.getY() && player.getPosition().getX() == r.getEnd().getX() && vector.getX() == 0) || (v.getY() == vector.getY() - 1 && vector.getX() == 0)) {
						tmpVector = v;
					}
					break;
				case SOUTH:
					if ((v.getY() == vector.getY() && player.getPosition().getX() == r.getEnd().getX() && vector.getX() == 0) || (v.getY() == vector.getY() - 1 && vector.getX() == 0)) {
						tmpVector = v;
					}
					break;
				case WEST:
					if ((v.getX() == vector.getX() && player.getPosition().getY() == r.getEnd().getY() && vector.getY() == 0) || (v.getX() == vector.getX() - 1 && vector.getY() == 0)) {
						tmpVector = v;
					}
					break;
				case EAST:
					if ((v.getX() == vector.getX() && player.getPosition().getY() == r.getEnd().getY() && vector.getY() == 0) || (v.getX() == vector.getX() - 1 && vector.getY() == 0)) {
						tmpVector = v;
					}
					break;
				}
			}
			if (tmpVector != null) {
				return tmpVector;
			}
			int min = Integer.MAX_VALUE;
			for (Vector v : solutionList) {
				switch (r.getOrientation()) {
				case NORTH:
					if (v.getY() == vector.getY() - 1 && v.getX() < min) {
						tmpVector = v;
						min = v.getX();
					}
					break;
				case SOUTH:
					if (v.getY() >= vector.getY() + 1) {
						tmpVector = v;
					}
					break;
				case WEST:
					if (v.getX() <= vector.getX() + 1) {
						tmpVector = v;
					}
					break;
				case EAST:
					if (v.getX() >= vector.getX() - 1) {
						tmpVector = v;
					}
					break;
				}
			}
			return tmpVector;
		}
		Position endPosition = r.getEnd();
		for (Vector v : solutionList) {
			Position tmp = new Position(player.getPosition().getX() + v.getX(), player.getPosition().getY() - v.getY());
			if (endPosition.equals(tmp) && !mapManager.getCase(tmp.getY(), tmp.getX()).isOccuped()) {
				playerRoadPosition.put(player.getId(), roadPosition + 1);
				return v;
			}
		}

		int minValue = Integer.MAX_VALUE;
		for (Vector v : solutionList) {
			Position tmp = new Position(player.getPosition().getX() + v.getX(), player.getPosition().getY() - v.getY());
			int val = getNextVector(v, tmp, r.getInitialOrientation(), r.getOrientation(), endPosition, 1);
			if (val < minValue) {
				minValue = val;
				vector = v;
			}
		}
		
		playerRoadPosition.put(player.getId(), roadPosition + 1);
		return vector;
	}
	
	private int getNextVector(Vector vector, Position position, Orientation currentOrientation, Orientation nextOrientation, Position endPosition, int step) {
		List<Vector> list = getVectorsSolution(position, vector);
		
		for (Vector vect : list) {
			switch(currentOrientation) {
			case NORTH:
				switch(nextOrientation) {
				case EAST:
					if (vect.getX() < 0 || vect.getY() < 0) {
						continue;
					}
					break;
				case WEST:
					if (vect.getX() > 0 || vect.getY() < 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			case SOUTH:
				switch(nextOrientation) {
				case EAST:
					if (vect.getX() < 0 || vect.getY() > 0) {
						continue;
					}
					break;
				case WEST:
					if (vect.getX() < 0 || vect.getY() > 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			case EAST:
				switch(nextOrientation) {
				case NORTH:
					if (vect.getY() < 0 || vect.getX() < 0) {
						continue;
					}
					break;
				case SOUTH:
					if (vect.getY() > 0 || vect.getX() < 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			case WEST:
				switch(nextOrientation) {
				case NORTH:
					if (vect.getY() < 0 || vect.getX() > 0) {
						continue;
					}
					break;
				case SOUTH:
					if (vect.getY() > 0 || vect.getX() > 0) {
						continue;
					}
					break;
				default:
					break;
				}
				break;
			}
			
			Position tmpPos = new Position(position.getX() + vect.getX(), position.getY() - vect.getY());
			if (endPosition.equals(tmpPos)) {
				return step;
			}
			return getNextVector(vect, tmpPos, currentOrientation, nextOrientation, endPosition, step+1);
		}
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Return the adding length of the vector to run as fastest as possible the straight line.
	 * @param distance the total distance to run
	 * @param vitesse the current speed.
	 * @return the number to add to the current vector.
	 */
	private int getNextPlay(int distance, int vitesse) {
		log.trace("getNextPlay({}, {})", distance, vitesse);
		int v = Math.abs(vitesse);
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
	
	
	private List<Vector> getVectorsPossibilities(Player player) {
		log.trace("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);
		log.trace("Player {} , init vector {}", player.toString(), player.getVector().toString());
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(player.getVector());
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX() - 1, player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(new Vector(player.getVector().getX() - 1, player.getVector().getY()));
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX() + 1, player.getPosition().getY() - player.getVector().getY(), player)) {
			list.add(new Vector(player.getVector().getX() + 1, player.getVector().getY()));
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY() + 1, player)) {
			list.add(new Vector(player.getVector().getX(), player.getVector().getY() - 1));
		}
		
		if (isMovingAvailable(player.getPosition().getX() + player.getVector().getX(), player.getPosition().getY() - player.getVector().getY() - 1, player)) {
			list.add(new Vector(player.getVector().getX(), player.getVector().getY() + 1));
		}
		log.debug("number of vectors possible : {}", list.size());
		for(Vector v : list) {
			log.trace(v.toString());
		}
		log.trace("getVectorsPossibilities filtering exiting");
		return vectorListFiltered(player.getPosition(), list);
	}

	
	private List<Vector> getVectorsSolution(Position position, Vector vector) {
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
	
	private List<Vector> vectorListFiltered(Position position, List<Vector> list) {
		int marge = MainFrame.MAP_MARGIN / 2;
		int xTrayPanel = position.getX() + marge;
		int yTrayPanel = position.getY() + marge;
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
	
	
	
	private boolean isMovingAvailable(int xMove, int yMove) {
		CaseModel model = mapManager.getCase(yMove, xMove);
		if (model != null && model.getField() == Field.GRASS) {
			return false;
		}
		return true;
	}
	
	private boolean isMovingAvailable(int xMove, int yMove, Player player) {
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
	
	/**
	 * Return the number of step to run the distance depending the speed.
	 * This method is recursively called with a variation of speed from 1, 0 or -1.
	 * @param distance the distance to run
	 * @param vitesse the current speed
	 * @param step the current number of step
	 * @return the number of step to run the distance
	 */
	private int getNbStep(int distance, int vitesse, int step) {
		if ((distance == 0 || distance == 1) && vitesse == 1) {
			return step;
		}
		if (distance < 0 || vitesse <= 0) {
			return Integer.MAX_VALUE;
		}
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step + 1);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step + 1);
		int nbMore = getNbStep(distance - vitesse, vitesse + 1, step + 1);
		return Math.min(nbMore, Math.min(nbLess, nbEqual));
	}
	
	private int getFirstMove(int distance) {
		log.debug("getFirstMove : distance = {}", Integer.toString(distance));
		Map<Integer,Integer> distanceMap = new HashMap<Integer,Integer>();
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
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step +1);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step + 1);
		return Math.min(nbLess, nbEqual);
	}

	public boolean isLastPlay() {
		return isLastPlay;
	}
	
	@Override
	public void reinitIsLastPlay() {
		this.isLastPlay = false;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition) {
		DetailledRoadDirectionInformation info = mapManager.getDetailledRoadDirectionInformationList().get(0);
		
		Vector vector = null;

		if (mapManager.getDetailledRoadDirectionInformationList().size() == 1) {
			switch (info.getOrientation()) {
			case NORTH:
				vector = new Vector(0, mapManager.getMapSize());
				break;
			case WEST:
				vector = new Vector(-mapManager.getMapSize(), 0);
				break;
			case SOUTH:
				vector = new Vector(0, -mapManager.getMapSize());
				break;
			case EAST:
				vector = new Vector(mapManager.getMapSize(), 0);
				break;
			}
			playerRoadPosition.put(player.getId(), 0);
		} else {
			int len = info.getLength();
			if (info.getInitialOrientation() == info.getOrientation() && len > 5) {
				int val = getFirstMove(len);
				log.trace("length of the road : {}, road :{}", len, info.toString());
				log.trace("length of the first move found : {}", val);
				switch (info.getOrientation()) {
				case NORTH:
					vector = new Vector(0, val);
					break;
				case WEST:
					vector = new Vector(-val, 0);
					break;
				case SOUTH:
					vector = new Vector(0, -val);
					break;
				case EAST:
					vector = new Vector(val, 0);
					break;
				}
				playerRoadPosition.put(player.getId(), 0);
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
	
	
	
	/*@Override
	public Vector getFirstMove(Player player, Map<Integer, Integer> playerRoadPosition) {
		int len = mapManager.getDetailledRoadDirectionInformationList().get(0).getLength();
		if (len == 0) {
			len = mapManager.getDetailledRoadDirectionInformationList().get(1).getLength();
		}
		log.trace("length of the road : {}, orientation:{}", len, mapManager.getDetailledRoadDirectionInformationList().get(0).toString());
		Vector vector = null;

		int val = getFirstMove(len);
		log.trace("length of the first move found : {}", val);

		if (val != 1) {
			if (mapManager.getDetailledRoadDirectionInformationList().size() == 1) {
				switch (mapManager.getDetailledRoadDirectionInformationList().get(0).getOrientation()) {
				case NORTH:
					vector = new Vector(0, mapManager.getMapSize());
					break;
				case WEST:
					vector = new Vector(-mapManager.getMapSize(), 0);
					break;
				case SOUTH:
					vector = new Vector(0, -mapManager.getMapSize());
					break;
				case EAST:
					vector = new Vector(mapManager.getMapSize(), 0);
					break;
				}
				playerRoadPosition.put(player.getId(), 0);
			} else {
				switch (mapManager.getDetailledRoadDirectionInformationList().get(0).getOrientation()) {
				case NORTH:
					vector = new Vector(0, val);
					break;
				case WEST:
					vector = new Vector(-val, 0);
					break;
				case SOUTH:
					vector = new Vector(0, -val);
					break;
				case EAST:
					vector = new Vector(val, 0);
					break;
				}
				playerRoadPosition.put(player.getId(), 0);
				
				Position startPosition = player.getPosition();
				CaseModel model = mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() + vector.getX());
				if (model.isOccuped()) {
					//Il serait judicieux de prendre la décision suivant le prochain virage......
					switch (mapManager.getDetailledRoadDirectionInformationList().get(0).getOrientation()) {
					case NORTH:
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).isOccuped()) {
							vector = new Vector(-1, val);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).isOccuped()) {
							vector = new Vector(1,  val);
						}
					case SOUTH:
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() - 1).isOccuped()) {
							vector = new Vector(-1, -val);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY(), startPosition.getX() - vector.getX() + 1).isOccuped()) {
							vector = new Vector(1, -val);
						}
						break;
					case WEST:
						if (mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(-val, -1);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(-val, 1);
						}
						break;
					case EAST:
						if (mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() - 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(val, -1);
						}
						if (mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).getField() != Field.GRASS && !mapManager.getCase(startPosition.getY() - vector.getY() + 1, startPosition.getX() - vector.getX()).isOccuped()) {
							vector = new Vector(val, 1);
						}
						break;
					}
					
				}
			}
		} else {
			
			RoadDirectionInformation r = mapManager.getDetailledRoadDirectionInformationList().get(0);
			RoadDirectionInformation nextRoadDirection = mapManager.getDetailledRoadDirectionInformationList().get(1);
			int curveLen = nextRoadDirection.getLength();
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
			playerRoadPosition.put(player.getId(), 1);
			
		}
		return vector;
	}*/
	
	private int getHighDistance(int distance) {
		int sum = 0;
		int value = 1;
		while(sum + value <= distance) {
			sum += value;
			value++;
		}
		return value - 1;
	}

	@Override
	public Position getStartingPosition() {
		List<Position> list = mapManager.getStartingPositionList();
		if (mapManager.getRoadDirectionInformationList().size() == 1) {
			return list.remove(0);
		}
		int index = Math.round(list.size() / 2);
		return list.remove(index);
	}

}
