package fr.ickik.formulamath.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.Field;
import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.Orientation;
import fr.ickik.formulamath.Player;
import fr.ickik.formulamath.PlayerType;
import fr.ickik.formulamath.Position;
import fr.ickik.formulamath.RoadDirectionInformation;
import fr.ickik.formulamath.Vector;
import fr.ickik.formulamath.controler.UpdateCaseListener;

/**
 * The class which manages all players.
 * @author Ickik.
 * @version 0.1.002, 17 oct. 2011.
 */
public class PlayerManager {

	private final List<Player> playerList;
	private int indexPlayerGame = 0;
	private MapManager mapManager;
	private boolean fireUpdateCaseListener;
	private boolean isWinner = false;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();
	private final Map<Integer, Integer> playerRoadPosition = new HashMap<Integer, Integer>();
	public static final int NUMBER_OF_PLAYER_MAX = 4;
	private static final PlayerManager singleton = new PlayerManager();
	private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);

	private PlayerManager() {
		playerList = new ArrayList<Player>(NUMBER_OF_PLAYER_MAX);
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
		log.debug("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);

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
		log.debug("number of vectors : {}", list.size());
		log.debug("getVectorsPossibilities exiting");
		return list;
	}
	
	private boolean isMovingAvailable(int xMove, int yMove, Player player) {
		CaseModel model = mapManager.getCase(yMove, xMove);
		if (model != null && model.getField() != Field.GRASS 
				&& (!model.isOccuped() || model.getIdPlayer() == player.getId())) {
			return true;
		}
		return false;
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
	
	public void lastPlay(Vector vector) {
		List<Position> endLineList = mapManager.getFinishingLinePositionList();
		Player p = getCurrentPlayer();
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
//		p.getPosition().setX(p.getPosition().getX() + vector.getX());
//		p.getPosition().setY(p.getPosition().getY() - vector.getY());
//		p.getVector().setX(vector.getX());
//		p.getVector().setY(vector.getY());
		mapManager.getCase(endLineList.get(0).getY(), endLineList.get(0).getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		//AIPlay();
		isWinner = true;
	}
	
	public void AIPlay() {
		while (playerList.get(indexPlayerGame).getType() == PlayerType.COMPUTER) {
			Player p = getCurrentPlayer();
			log.debug("Player {} is under playing", p.toString());
			playerRoadPosition.put(p.getId(), 0);
			int roadPosition = playerRoadPosition.get(p.getId());
			RoadDirectionInformation r = mapManager.getRoadDirectionInformationList().get(roadPosition);
			int len = r.getLengthToEnd(p.getPosition());
			Vector vector = null;
			if (len == 1) {
				RoadDirectionInformation nextRoadDirection = mapManager.getRoadDirectionInformationList().get(roadPosition + 1);
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
			} else {
				switch (r.getOrientation()) {
				case NORTH:
				case SOUTH:
					int d = getNextPlay(len, p.getVector().getY());
					vector = new Vector(0, p.getVector().getY() + d);
					break;
				case WEST:
				case EAST:
					d = getNextPlay(len, p.getVector().getX());
					vector = new Vector(p.getVector().getX() + d, 0);
					break;
				}
			}
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
			p.getPosition().setX(p.getPosition().getX() + vector.getX());
			p.getPosition().setY(p.getPosition().getY() - vector.getY());
			p.getVector().setX(vector.getX());
			p.getVector().setY(vector.getY());
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
		indexPlayerGame++;
		indexPlayerGame = indexPlayerGame % playerList.size();
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
				log.debug("boucle while");
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
					int val = getFirstMove(len);
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
					playerRoadPosition.put(p.getId(), 0);
					play(vector);
					updateIndexPlayerGame();
					fireUpdateCaseListener(p);
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	private int getFirstMove(int distance) {
		Map<Integer,Integer> distanceList = new HashMap<Integer,Integer>();
		int halfDistance = distance / 2;
		for (int i = 1; i < halfDistance; i++) {
			distanceList.put(getNbStepFirstMove(distance, i, 0), i);
		}
		List<Integer> list = new ArrayList<Integer>(distanceList.keySet());
		Collections.sort(list);
		return distanceList.get(list.get(0));
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
		fireUpdateCaseListener(getCurrentPlayer());
		updateIndexPlayerGame();
		return indexPlayerGame % getPlayerList().size() != 0;
	}
	
	/**
	 * Update the position of the player, depending the index of the available positions.
	 * @param p the player to update.
	 * @param index
	 * @throws FormulaMathException
	 */
	public void updatePlayer(Player p, int index) {
		List<Position> list = mapManager.getStartingPositionList();
		p.getPosition().setX(list.get(index).getX());
		p.getPosition().setY(list.get(index).getY());
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
	
	protected void fireUpdateCaseListener(Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerCase(p);
		}
	}
	
	protected void fireUpdatePossibilitiesListener(Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerPossibilities(p);
		}
	}

	protected void fireEndGameListener(Player player) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updateEndGamePanel(player);
		}
	}
	
	public void setMapManager(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	public boolean existsHumanPlayer() {
		for(Player player : playerList) {
			if (player.getType() == PlayerType.HUMAN) {
				return true;
			}
		}
		return false;
	}
}
