package fr.ickik.formulamath.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.controler.UpdateCaseListener;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;

/**
 * The class which manages all players.
 * @author Ickik.
 * @version 0.2.001, 15 mai 2012.
 */
public final class PlayerManager {

	private final List<Player> playerList;
	private final List<Player> finishPositionList;
	private int indexPlayerGame = 0;
	private MapManager mapManager;
	private boolean fireUpdateCaseListener;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();
	//private AILevel computerLevel;
	
	/**
	 * Map representing the index of the user (represented by the id) in the road model.
	 */
	private final Map<Integer, Integer> playerRoadPosition = new HashMap<Integer, Integer>();
	public static final int NUMBER_OF_PLAYER_MAX = 4;
	private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);
	
	public PlayerManager(MapManager mapManager) {
		this.mapManager = mapManager;
		playerList = new ArrayList<Player>(NUMBER_OF_PLAYER_MAX);
		finishPositionList = new ArrayList<Player>(NUMBER_OF_PLAYER_MAX);
		for (int i = 0; i < NUMBER_OF_PLAYER_MAX; i++) {
			finishPositionList.add(null);
		}
	}

	/**
	 * Add a {@link Player} in the manager.
	 * @param player the player to add to the manager.
	 */
	public void addPlayer(Player player) {
		if (player == null) {
			log.warn("Trying to add nul player in the player list");
			return;
		}
		playerList.add(player);
	}
	
	/**
	 * Return the list of the {@link Player}.
	 * @return the list of the player.
	 */
	public List<Player> getPlayerList() {
		return playerList;
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
		log.trace("getVectorsPossibilities exiting");
		return list;
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
	
	public void play(Vector vector) {
		Player p = getCurrentPlayer();
		if (vector == null) {
			addFinishPlayer(p, false);
			updateIndexPlayerGame();
			computerPlay();
			return ;
		}
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		p.getPosition().setX(p.getPosition().getX() + vector.getX());
		p.getPosition().setY(p.getPosition().getY() - vector.getY());
		p.getVector().setX(vector.getX());
		p.getVector().setY(vector.getY());
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		computerPlay();
	}
	
	/**
	 * Moves the last time the current player.
	 * @param vector the last vector to move.
	 */
	public void lastPlay(Vector vector) {
		log.debug("LastPlay {}", vector);
		List<Position> endLineList = mapManager.getFinishingLinePositionList();
		Player p = getCurrentPlayer();
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		mapManager.getCase(endLineList.get(0).getY(), endLineList.get(0).getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		addFinishPlayer(p, true);
		computerPlay();
	}
	
	private void addFinishPlayer(Player p, boolean isWinning) {
		int begin, end;
		if (isWinning) {
			begin = 0;
			end = NUMBER_OF_PLAYER_MAX;
		} else {
			begin = NUMBER_OF_PLAYER_MAX;
			end = 0;
		}
		for (int i = begin; i < end; i = (isWinning) ? i+1 : i-1) {
			if (finishPositionList.get(i) == null) {
				finishPositionList.set(i, p);
			}
		}
	}
	
	/**
	 * Iterates all AI players and set new available position.
	 */
	public void computerPlay() {
		while (getCurrentPlayer().getType() == PlayerType.COMPUTER && !finishPositionList.contains(getCurrentPlayer())) {
			Player p = getCurrentPlayer();
			log.debug("AI Player {} is under playing", p.toString());
			
			int roadPosition = playerRoadPosition.get(p.getId());
			RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
			int len = r.getLengthToEnd(p.getPosition()) - 1;
			Vector vector = null;
			log.debug("AI rest length of the vector:{}", len);
			log.trace("Orientation: {}", r.getOrientation());
			if ((Math.abs(len) == 1 || len == 0) && (p.getVector().getX() == 1 ||  p.getVector().getX() == -1 || p.getVector().getY() == 1 || p.getVector().getY() == -1)) {
				RoadDirectionInformation nextRoadDirection;
				if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
					nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
				} else {
					nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
				}
				playerRoadPosition.put(p.getId(), roadPosition + 1);
				log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
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
			} else if ((p.getVector().getX() == 1 ||  p.getVector().getX() == -1) && (p.getVector().getY() == 1 || p.getVector().getY() == -1)) {
				RoadDirectionInformation nextRoadDirection;
				if (mapManager.getRoadDirectionInformationList().size() > roadPosition + 1) {
					nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
					//playerRoadPosition.put(p.getId(), roadPosition + 1);
				} else {
					nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition);
					//playerRoadPosition.put(p.getId(), roadPosition);
				}
				log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
				switch (r.getOrientation()) {
				case NORTH:
//					if (nextRoadDirection.getOrientation() == Orientation.EAST) {
//						vector = new Vector(1, 0);
//					} else if (nextRoadDirection.getOrientation() == Orientation.WEST) {
//						vector = new Vector(-1, 0);
//					} else {
						vector = new Vector(0, 1);
//					}
					break;
				case SOUTH:
//					if (nextRoadDirection.getOrientation() == Orientation.EAST) {
//						vector = new Vector(-1, 0);
//					} else if (nextRoadDirection.getOrientation() == Orientation.WEST) {
//						vector = new Vector(1, 0);
//					} else {
						vector = new Vector(0, -1);
//					}
					break;
				case WEST:
//					if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
//						vector = new Vector(-1, 0);
////						vector = new Vector(0, 1);
//					} else if (nextRoadDirection.getOrientation() == Orientation.SOUTH) {
//						vector = new Vector(0, -1);
//					} else {
						vector = new Vector(-1, 0);
//					}
					break;
				case EAST:
//					if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
//						vector = new Vector(0, 1);
//					} else if (nextRoadDirection.getOrientation() == Orientation.SOUTH) {
//						vector = new Vector(0, -1);
//					} else {
						vector = new Vector(1, 0);
//					}
					break;
				}
			} else {
				log.debug("Go in the same direction {}", r.getOrientation());
				if (roadPosition == mapManager.getRoadDirectionInformationList().size() - 1) {
					log.trace("Last direction, final sprint");
					switch (r.getOrientation()) {
					case NORTH:
						vector = new Vector(0, p.getVector().getY() + 1);
						break;
					case SOUTH:
						vector = new Vector(0, p.getVector().getY() - 1);
						break;
					case WEST:
						vector = new Vector(p.getVector().getX() - 1, 0);
						break;
					case EAST:
						vector = new Vector(p.getVector().getX() + 1, 0);
						break;
					}
				} else {
					switch (r.getOrientation()) {
					case NORTH:
						int d = getNextPlay(len, p.getVector().getY());
						log.debug("Next play : {}", d);
						vector = new Vector(0, p.getVector().getY() + d);
						break;
					case SOUTH:
						d = getNextPlay(len, p.getVector().getY());
						log.debug("Next play : {}", d);
						vector = new Vector(0, p.getVector().getY() - d);
						break;
					case WEST:
						d = getNextPlay(len, p.getVector().getX());
						log.debug("Next play : {}", d);
						vector = new Vector(p.getVector().getX() - d, 0);
						break;
					case EAST:
						d = getNextPlay(len, p.getVector().getX());
						log.debug("Next play : {}", d);
						vector = new Vector(p.getVector().getX() + d, 0);
						break;
					}
				}
			}
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
			log.debug("Player initial position: {}", p.getPosition());
			log.debug("Player last vector {}", p.getVector());
			log.debug("{}", vector);
			for (int i = 0; i <= vector.getX(); i++) {
				for (int j = 0; j <= vector.getY(); j++) {
					if (p.getPosition().getX() + i >= 0 && p.getPosition().getX() + i < mapManager.getMapSize() && p.getPosition().getY() - j >= 0 && p.getPosition().getY() - j < mapManager.getMapSize()) {
						CaseModel model = mapManager.getCase(p.getPosition().getY() - j, p.getPosition().getX() + i);
						if (model.getField() == Field.FINISHING_LINE) {
							lastPlay(new Vector(i, j));
							fireEndGameListener();
							return;
						}
					}
				}
			}
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
			p.getPosition().setX(p.getPosition().getX() + vector.getX());
			p.getPosition().setY(p.getPosition().getY() - vector.getY());
			p.getVector().setX(vector.getX());
			p.getVector().setY(vector.getY());
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
			updateIndexPlayerGame();
			/*p.getPosition().setX(p.getPosition().getX() + vector.getX());
			p.getPosition().setY(p.getPosition().getY() - vector.getY());
			p.getVector().setX(vector.getX());
			p.getVector().setY(vector.getY());
			log.debug("Player new position: {}", p.getPosition());
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
			
			fireUpdateCaseListener(p);
			updateIndexPlayerGame();*/
		}
		fireDisplayPlayerMovePossibilities();
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
		step++;
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step);
		int nbMore = getNbStep(distance - vitesse, vitesse + 1, step);
		return Math.min(nbMore, Math.min(nbLess, nbEqual));
	}
	
	private void updateIndexPlayerGame() {
		if (playerList.size() == getNumberOfFinishPlayer()) {
			fireEndGameListener();
			return ;
		}
		do {
			indexPlayerGame++;
			indexPlayerGame = indexPlayerGame % playerList.size();
		} while(finishPositionList.contains(playerList.get(indexPlayerGame)));
	}
	
	private int getNumberOfFinishPlayer() {
		int nb = 0;
		for (int i = 0; i < finishPositionList.size(); i++) {
			if (finishPositionList.get(i) != null) {
				nb++;
			}
		}
		return nb;
	}
	
	public void initStartPosition() {
		log.debug("initStartPosition entering");
		List<Position> list = mapManager.getStartingPositionList();
		List<Player> playerList = getPlayerList().subList(getPlayerList().indexOf(getCurrentPlayer()), getPlayerList().size());
		if (MapManager.ROAD_SIZE - list.size() == getPlayerList().size()) {
			log.trace("No player to init, display first move panel");
			initFirstMove();
			return ;//prevenir la vue qu'elle doit jouer
		}
		log.debug("number of start position : {}", list.size());
		if (!playerList.isEmpty()) {
			Iterator<Player> it = playerList.iterator();
			while(it.hasNext()) {
				log.trace("boucle while");
				Player p = it.next();
				if (p.getType().equals(PlayerType.COMPUTER)) {
					log.debug("computer");
					p.getPosition().setX(list.get(0).getX());
					p.getPosition().setY(list.get(0).getY());
					updateIndexPlayerGame();
					log.debug("computer start position : ({}, {})", p.getPosition().getX(), p.getPosition().getY());
					mapManager.getCase(list.get(0).getY(), list.get(0).getX()).setIdPlayer(p.getId());
					list.remove(0);
					fireUpdateCaseListener(p);
					log.debug("initStartPosition fire for Computer");
				} else {
					log.debug("Human start position");
					fireDisplayPlayerPossibilities(p, list);
					return ;
				}
			}
		}
		log.debug("initStartPosition exiting");
		log.debug("No player to place");
		initFirstMove();
	}
	
	private void fireDisplayPlayerPossibilities(Player player, List<Position> list) {
		for(UpdateCaseListener l : updateCaseListenerList) {
			l.displayPlayerStartingPossibilities(player, list, mapManager.getMapSize());
		}
	}
	
	private void fireDisplayPlayerFirstMovePossibilities(Player p) {
		for(UpdateCaseListener l : updateCaseListenerList) {
			l.displayPlayerFirstMove(p, mapManager.getMapSize());
		}
	}
	
	private void fireDisplayPlayerMovePossibilities() {
		List<Vector> list = getVectorsPossibilities(getCurrentPlayer());
		if (list.isEmpty()) {
			addFinishPlayer(getCurrentPlayer(), false);
		}
		for(UpdateCaseListener l : updateCaseListenerList) {
			l.displayPlayerMovePossibilities(getCurrentPlayer(), list, mapManager.getMapSize());
		}
	}
	
	public void initFirstMove() {
		log.debug("initAIFirstMove");
		List<Player> playerList = getPlayerList().subList(getPlayerList().indexOf(getCurrentPlayer()), getPlayerList().size());
		if (!playerList.isEmpty()) {
			Iterator<Player> it = playerList.iterator();
			while(it.hasNext()) {
				Player p = it.next();
				if (p.getType().equals(PlayerType.COMPUTER)) {
					log.debug("Computer first move");
					int len = mapManager.getRoadDirectionInformationList().get(0).getLength() - 1;
					log.trace("length of the road : {}, orientation:{}", len, mapManager.getRoadDirectionInformationList().get(0).toString());
					int val = getFirstMove(len);
					log.trace("length of the first move found : {}", val);
					Vector vector = null;
					if (val != 1) {
						switch (mapManager.getRoadDirectionInformationList().get(0).getOrientation()) {
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
						playerRoadPosition.put(p.getId(), 0);
					} else {
						RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(0);
						RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(1);
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
						playerRoadPosition.put(p.getId(), 1);
					}
					log.debug("Vector determined : {}", vector.toString());
					log.debug("AI initial position {}", p.getPosition().toString());
					mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
					p.getPosition().setX(p.getPosition().getX() + vector.getX());
					p.getPosition().setY(p.getPosition().getY() - vector.getY());
					p.getVector().setX(vector.getX());
					p.getVector().setY(vector.getY());
					mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
					updateIndexPlayerGame();
					log.debug("AI new position {}", p.getPosition().toString());
					fireUpdateCaseListener(p);
				} else {
					log.debug("Human turn first move");
					fireDisplayPlayerFirstMovePossibilities(p);
					return ;
				}
			}
		}
		computerPlay();
	}
	
	public void firstMove(Vector vector) {
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		getCurrentPlayer().getPosition().setX(getCurrentPlayer().getPosition().getX() + vector.getX());
		getCurrentPlayer().getPosition().setY(getCurrentPlayer().getPosition().getY() - vector.getY());
		getCurrentPlayer().getVector().setX(vector.getX());
		getCurrentPlayer().getVector().setY(vector.getY());
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(getCurrentPlayer().getId());
		playerRoadPosition.put(getCurrentPlayer().getId(), 0);
		fireUpdateCaseListener(getCurrentPlayer());
		updateIndexPlayerGame();
		initFirstMove();
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
	
	private int getHighDistance(int distance) {
		int sum = 0;
		int value = 1;
		while(sum + value <= distance) {
			sum += value;
			value++;
		}
		return value - 1;
	}
	
	private int getNbStepFirstMove(int distance, int vitesse, int step) {
		if (distance == 0 && vitesse == 1) {
			return step;
		}
		if (distance < 0 || vitesse <= 0) {
			return Integer.MAX_VALUE;
		}
		step++;
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step);
		return Math.min(nbLess, nbEqual);
	}
	
	/**
	 * Update the starting position of the player, depending the index of the available positions.
	 * @param p the player to update.
	 * @param index the index in the list of available position.
	 */
	public void updateStartPositionPlayer(Position position) {
		log.trace("updateStartPositionPlayer(index= {} for player {})", Integer.toString(getCurrentPlayer().getId()) + " - " + getCurrentPlayer().getName(), position);
		List<Position> list = mapManager.getStartingPositionList();
		list.remove(position);
		getCurrentPlayer().getPosition().setX(position.getX());
		getCurrentPlayer().getPosition().setY(position.getY());
		log.debug("Player starting position {}", getCurrentPlayer().toString());
		mapManager.getCase(position.getY(), position.getX()).setIdPlayer(getCurrentPlayer().getId());
		fireUpdateCaseListener(getCurrentPlayer());
		updateIndexPlayerGame();
		initStartPosition();
	}
	
	public Player getCurrentPlayer() {
		return playerList.get(indexPlayerGame);
	}

	public void addUpdateCaseListener(UpdateCaseListener updateCaseListener) {
		if (updateCaseListener == null) {
			return;
		}
		updateCaseListenerList.add(updateCaseListener);
	}

	public void removeUpdateCaseListener(UpdateCaseListener updateCaseListener)
			throws FormulaMathException {
		if (fireUpdateCaseListener) {
			throw new FormulaMathException();
		} else {
			updateCaseListenerList.remove(updateCaseListener);
		}
	}
	
	private void fireUpdateCaseListener(Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerCase();
		}
	}

	private void fireEndGameListener() {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updateEndGamePanel();
		}
	}

	/**
	 * Return the list of player's position at the end. This
	 * list is sorted by position.
	 * @return the player's position list.
	 */
	public List<Player> getFinishPositionList() {
		return finishPositionList;
	}

	public void reinitialization() {
		playerList.clear();
		finishPositionList.clear();
		playerRoadPosition.clear();
		indexPlayerGame = 0;
	}

//	public void setComputerLevel(AILevel computerLevel) {
//		this.computerLevel = computerLevel;
//	}
}
