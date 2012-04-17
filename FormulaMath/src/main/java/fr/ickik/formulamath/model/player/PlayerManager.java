package fr.ickik.formulamath.model.player;

import java.awt.Color;
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
 * @version 0.1.006, 10 apr. 2012.
 */
public final class PlayerManager {

	private final List<Player> playerList;
	private final List<Player> finishPositionList;
	private int indexPlayerGame = 0;
	private MapManager mapManager;
	private boolean fireUpdateCaseListener;
	private boolean isWinner = false;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();
	
	/**
	 * Map representing the index of the user (represented by the id) in the road model.
	 */
	private final Map<Integer, Integer> playerRoadPosition = new HashMap<Integer, Integer>();
	public static final int NUMBER_OF_PLAYER_MAX = 4;
	private static final PlayerManager singleton = new PlayerManager();
	private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);

	private PlayerManager() {
		playerList = new ArrayList<Player>(NUMBER_OF_PLAYER_MAX);
		finishPositionList = new ArrayList<Player>(NUMBER_OF_PLAYER_MAX);
		for (int i = 0; i < NUMBER_OF_PLAYER_MAX; i++) {
			finishPositionList.add(null);
		}
	}

	/**
	 * Return the unique instance of the player manager.
	 * @return the unique instance of the player manager.
	 */
	public static PlayerManager getInstance() {
		return singleton;
	}

	/**
	 * Add a {@link Player} in the manager.
	 * @param player the player to add to the manager.
	 */
	public void addPlayer(Player player) {
		playerList.add(player);
	}
	
	/**
	 * Return the list of the {@link Player}.
	 * @return the list of the player.
	 */
	public List<Player> getPlayerList() {
		return playerList;
	}

	public List<Vector> getVectorsPossibilities(Player player) {
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
		return model == null;
	}

	public boolean play(Vector vector) {
		if (isWinner) {
			return false;
		}
		Player p = getCurrentPlayer();
		if (mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()) == null
				|| mapManager.getCase(p.getPosition().getY() - vector.getY(), p.getPosition().getX() + vector.getX()) == null) {
			return false;
		}
		
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		p.getPosition().setX(p.getPosition().getX() + vector.getX());
		p.getPosition().setY(p.getPosition().getY() - vector.getY());
		p.getVector().setX(vector.getX());
		p.getVector().setY(vector.getY());
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		AIPlay();
		return true;
	}
	
	/**
	 * 
	 * @param vector
	 */
	public void lastPlay(Vector vector) {
		log.debug("LastPlay {}", vector);
		List<Position> endLineList = mapManager.getFinishingLinePositionList();
		Player p = getCurrentPlayer();
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		mapManager.getCase(endLineList.get(0).getY(), endLineList.get(0).getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		//AIPlay();.
		addFinishPlayer(p, true);
		isWinner = true;
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
	
	public void AIPlay() {
		while (playerList.get(indexPlayerGame).getType() == PlayerType.COMPUTER) {
			Player p = getCurrentPlayer();
			log.debug("AI Player {} is under playing", p.toString());
			
			int roadPosition = playerRoadPosition.get(p.getId());
			RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
			int len = r.getLengthToEnd(p.getPosition());
			Vector vector = null;
			log.debug("rest length of the vector:{}", len);
			log.trace("Orientation: {}", r.getOrientation());
			if (len == 1 && (p.getVector().getX() == 1 ||  p.getVector().getX() == -1 || p.getVector().getY() == 1 || p.getVector().getY() == -1)) {
				RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
				//playerRoadPosition.put(p.getId(), roadPosition + 1);
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
						vector = new Vector(-1, -1);
					} else {
						vector = new Vector(1, -1);
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
				RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
				playerRoadPosition.put(p.getId(), roadPosition + 1);
				log.trace("Next orientation: {}", nextRoadDirection.getOrientation());
				switch (r.getOrientation()) {
				case NORTH:
					if (nextRoadDirection.getOrientation() == Orientation.EAST) {
						vector = new Vector(1, 0);
					} else {
						vector = new Vector(-1, 0);
					}
					break;
				case SOUTH:
					if (nextRoadDirection.getOrientation() == Orientation.EAST) {
						vector = new Vector(-1, 0);
					} else {
						vector = new Vector(1, 0);
					}
					break;
				case WEST:
					if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
						vector = new Vector(0, 1);
					} else {
						vector = new Vector(0, -1);
					}
					break;
				case EAST:
					if (nextRoadDirection.getOrientation() == Orientation.NORTH) {
						vector = new Vector(0, 1);
					} else {
						vector = new Vector(0, -1);
					}
					break;
				}
			} else {
				log.debug("Go in the same direction {}", r.getOrientation());
				switch (r.getOrientation()) {
				case NORTH:
				case SOUTH:
					int d = getNextPlay(len, p.getVector().getY());
					log.debug("Next play : {}", d);
					vector = new Vector(0, p.getVector().getY() + d);
					break;
				case WEST:
				case EAST:
					d = getNextPlay(len, p.getVector().getX());
					log.debug("Next play : {}", d);
					vector = new Vector(p.getVector().getX() + d, 0);
					break;
				}
			}
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
			log.debug("Player initial position: {}", p.getPosition());
			log.debug("Vector {}", vector);
			p.getPosition().setX(p.getPosition().getX() + vector.getX());
			p.getPosition().setY(p.getPosition().getY() - vector.getY());
			p.getVector().setX(vector.getX());
			p.getVector().setY(vector.getY());
			log.debug("Player new position: {}", p.getPosition());
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
			
			fireUpdateCaseListener(p);
			updateIndexPlayerGame();
		}
		humanPlaying();
	}
	
	private int getNextPlay(int distance, int vitesse) {
		if (distance == 0) {
			if (vitesse == 2) {
				return -1;
			}
			return vitesse;
		}
		int nbLess = getNbStep(distance, vitesse - 1, 0);
		int nbEqual = getNbStep(distance, vitesse, 0);
		int nbMore = getNbStep(distance, vitesse + 1, 0);
		if (nbLess < nbEqual && nbLess < nbMore) {
			return -1;
		} else if (nbMore < nbEqual && nbMore < nbLess) {
			return 1;
		}
		return 0;
	}

	private int getNbStep(int distance, int vitesse, int step) {
		if (distance == 0 && vitesse == 1) {
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
	
	private void humanPlaying() {
		fireUpdatePossibilitiesListener(getCurrentPlayer());
	}

	public boolean initStartPosition() {
		log.debug("initStartPosition entering");
		List<Position> list = mapManager.getStartingPositionList();
		for (int i = 0; i < list.size(); ) {
			Position p = list.get(i);
			if (mapManager.getCase(p.getY(), p.getX()).isOccuped()) {
				list.remove(i);
			} else {
				i++;
			}
		}
		if (MapManager.ROAD_SIZE - list.size() == getPlayerList().size()) {
			return false;
		}
		log.debug("number of start position : {}", list.size());
		List<Player> playerList = getPlayerList().subList(getPlayerList().indexOf(getCurrentPlayer()), getPlayerList().size());
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
					log.debug("fire");
				} else {
					log.debug("Human start position");
					return true;
				}
			}
		}
		log.debug("initStartPosition exiting");
		return false;
	}
	
	public boolean initAIFirstMove() {
		log.debug("initAIFirstMove");
		List<Player> playerList = getPlayerList().subList(getPlayerList().indexOf(getCurrentPlayer()), getPlayerList().size());
		if (!playerList.isEmpty()) {
			Iterator<Player> it = playerList.iterator();
			while(it.hasNext()) {
				Player p = it.next();
				if (p.getType().equals(PlayerType.COMPUTER)) {
					log.debug("Computer first move");
					int len = mapManager.getRoadDirectionInformationList().get(0).getLength();
					log.trace("length of the road : {}, orientation:{}", len, mapManager.getRoadDirectionInformationList().get(0).toString());
					int val = getFirstMove(len);
					log.trace("length of the first move found : {}", val);
					Vector vector = null;
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
					log.debug("Vector determined : {}", vector.toString());
					log.debug("AI initial position {}", p.getPosition().toString());
					playerRoadPosition.put(p.getId(), 0);
					play(vector);
					log.debug("AI new position {}", p.getPosition().toString());
					fireUpdateCaseListener(p);
				} else {
					log.debug("Human turn first move");
					return true;
				}
			}
		}
		return false;
	}
	
	private int getFirstMove(int distance) {
		log.debug("getFirstMove : distance = {}", Integer.toString(distance));
		Map<Integer,Integer> distanceMap = new HashMap<Integer,Integer>();
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
		step++;
		int nbLess = getNbStep(distance - vitesse, vitesse - 1, step);
		int nbEqual = getNbStep(distance - vitesse, vitesse, step);
		return Math.min(nbLess, nbEqual);
	}
	
	public boolean initFirstMove(Vector vector) {
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		getCurrentPlayer().getPosition().setX(getCurrentPlayer().getPosition().getX() + vector.getX());
		getCurrentPlayer().getPosition().setY(getCurrentPlayer().getPosition().getY() - vector.getY());
		getCurrentPlayer().getVector().setX(vector.getX());
		getCurrentPlayer().getVector().setY(vector.getY());
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(getCurrentPlayer().getId());
		playerRoadPosition.put(getCurrentPlayer().getId(), 0);
		fireUpdateCaseListener(getCurrentPlayer());
		updateIndexPlayerGame();
		return indexPlayerGame % getPlayerList().size() != 0;
	}
	
	/**
	 * Update the starting position of the player, depending the index of the available positions.
	 * @param p the player to update.
	 * @param index the index in the list of available position.
	 */
	public void updateStartPositionPlayer(Player p, int index) {
		log.trace("updateStartPositionPlayer(index= {} for player {})", Integer.toString(p.getId()) + " - " + p.getName(), index);
		List<Position> list = mapManager.getStartingPositionList();
		p.getPosition().setX(list.get(index).getX());
		p.getPosition().setY(list.get(index).getY());
		log.debug("Player starting position {}", p.toString());
		mapManager.getCase(list.get(index).getY(), list.get(index).getX()).setIdPlayer(p.getId());
		updateIndexPlayerGame();
		fireUpdateCaseListener(p);
	}
	
	public Player getCurrentPlayer() {
		return playerList.get(indexPlayerGame);
	}

	/**
	 * Get the color of the player depending the id of them.
	 * @param idPlayer the id of the player.
	 * @return the {@link Color} associates to the player.
	 */
	public Color getColorById(int idPlayer) {
		return playerList.get(idPlayer - 1).getPlayerColor();
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
			u.updatePlayerCase(p);
		}
	}
	
	private void fireUpdatePossibilitiesListener(Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerPossibilities(p);
		}
	}

	/*private void fireEndGameListener(Player player) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updateEndGamePanel(player);
		}
	}*/

	private void fireEndGameListener() {
		for (UpdateCaseListener u : updateCaseListenerList) {
			//u.updateEndGamePanel();
			System.exit(0);
		}
	}
	
	public void setMapManager(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	/**
	 * Return the number of human player in the game.
	 * @return the number of human player in the game.
	 */
	public int getNumberOfHumanPlayer() {
		int humanNb = 0;
		for (Player p : getPlayerList()) {
			if (p.getType() == PlayerType.HUMAN) {
				humanNb++;
			}
		}
		return humanNb;
	}
	
	/**
	 * Check if a human player exists and return true.
	 * @return true if a human player exists, false otherwise.
	 */
	public boolean existsHumanPlayer() {
		return getNumberOfHumanPlayer() >= 1;
	}

	/**
	 * Return the list of player's position at the end. This
	 * list is sorted by position.
	 * @return the player's position list.
	 */
	public List<Player> getFinishPositionList() {
		return finishPositionList;
	}
}
