package fr.ickik.formulamath.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.Player;
import fr.ickik.formulamath.PlayerType;
import fr.ickik.formulamath.Position;
import fr.ickik.formulamath.Field;
import fr.ickik.formulamath.Vector;
import fr.ickik.formulamath.controler.UpdateCaseListener;

/**
 * The class which manages all players.
 * @author Ickik.
 * @version 0.1.000, 17 oct. 2011.
 */
public class PlayerManager {

	private final List<Player> playerList;
	private int indexPlayerGame = 0;
	private MapManager mapManager;
	private boolean fireUpdateCaseListener;
	private boolean isWinner = false;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();
	public static final int NUMBER_OF_PLAYER_MAX = 4;
	private static final PlayerManager singleton = new PlayerManager();
	private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);

	private PlayerManager() {
		playerList = new ArrayList<Player>(NUMBER_OF_PLAYER_MAX);
	}
	
	public static PlayerManager getInstance() {
		return singleton;
	}

	public void addPlayer(Player player) {
		playerList.add(player);
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public List<Vector> getVectorsPossibilities(Player player) {
		log.debug("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving()) != null
				&& mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving()).getField() != Field.HERBE) {
			list.add(player.getVector());
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() - 1) != null
				&& mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() - 1).getField() != Field.HERBE
				&& !mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() - 1).isOccuped()) {
			list.add(new Vector(player.getVector().getXMoving() - 1, player.getVector().getYMoving()));
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() + 1) != null
				&& mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() + 1).getField() != Field.HERBE
				&& !mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() + 1).isOccuped()) {
			list.add(new Vector(player.getVector().getXMoving() + 1, player.getVector().getYMoving()));
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() + 1, player.getPosition().getX() + player.getVector().getXMoving()) != null
				&& mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() + 1, player.getPosition().getX() + player.getVector().getXMoving()).getField() != Field.HERBE
				&& !mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() + 1, player.getPosition().getX() + player.getVector().getXMoving()).isOccuped()) {
			list.add(new Vector(player.getVector().getXMoving(), player.getVector().getYMoving() - 1));
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() - 1, player.getPosition().getX() + player.getVector().getXMoving()) != null
				&& mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() - 1, player.getPosition().getX() + player.getVector().getXMoving()).getField() != Field.HERBE
				&& !mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() - 1, player.getPosition().getX() + player.getVector().getXMoving()).isOccuped()) {
			list.add(new Vector(player.getVector().getXMoving(), player.getVector().getYMoving() + 1));
		}
		log.debug("number of vectors : {}", list.size());
		log.debug("getVectorsPossibilities exiting");
		return list;
	}

	public boolean play(Vector vector) {
		Player p = getCurrentPlayer();
		if (isWinner) {
			return false;
		}
		if (mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()) == null
				|| mapManager.getCase(p.getPosition().getY() - vector.getYMoving(), p.getPosition().getX() + vector.getXMoving()) == null) {
			return false;
		}
		
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		p.getPosition().setX(p.getPosition().getX() + vector.getXMoving());
		p.getPosition().setY(p.getPosition().getY() - vector.getYMoving());
		p.getVector().setXMoving(vector.getXMoving());
		p.getVector().setYMoving(vector.getYMoving());
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		AIPlay();
		return true;
	}
	
	public void AIPlay() {
		AIPlaying();
	}
	
	private void AIPlaying() {
		while (playerList.get(indexPlayerGame).getType() == PlayerType.COMPUTER) {
			Player p = getCurrentPlayer();
			log.debug("Player {} is under playing", p.toString());
			fireUpdateCaseListener(p);
			updateIndexPlayerGame();
		}
		humanPlaying();
	}
	
	private int getNextPlay(int distance, int vitesse) {
		if (distance == 0) {
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
		if (distance == 0) {
			return step;
		}
		if (distance < 0) {
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
		List<Position> list = mapManager.getStartPosition();
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
					//algo de recherche meilleur position
					updateIndexPlayerGame();
					fireUpdateCaseListener(p);
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean initFirstMove(Vector vector) {
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		getCurrentPlayer().getPosition().setX(getCurrentPlayer().getPosition().getX() + vector.getXMoving());
		getCurrentPlayer().getPosition().setY(getCurrentPlayer().getPosition().getY() - vector.getYMoving());
		getCurrentPlayer().getVector().setXMoving(vector.getXMoving());
		getCurrentPlayer().getVector().setYMoving(vector.getYMoving());
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
		List<Position> list = mapManager.getStartPosition();
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
	
	public boolean isHumanPlayer() {
		for(Player player : playerList) {
			if (player.getType() == PlayerType.HUMAN) {
				return true;
			}
		}
		return false;
	}
}
