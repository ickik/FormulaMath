package fr.ickik.formulamath.model.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.controler.UpdateCaseListener;
import fr.ickik.formulamath.entity.InformationMessage;
import fr.ickik.formulamath.entity.MessageType;
import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.CaseModel;
import fr.ickik.formulamath.model.InformationModel;
import fr.ickik.formulamath.model.ai.AILevel;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;

/**
 * The class which manages all players.
 * @author Ickik.
 * @version 0.3.003, 3 September 2012.
 */
public final class PlayerManager {

	private final List<Player> playerList;
	private final List<Player> finishPositionList;
	private int indexPlayerGame = 0;
	private final MapManager mapManager;
	private InformationModel informationModel;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();
	private AILevel computerLevel;
	
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
			log.warn("Trying to add null player in the player list");
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
	
	/**
	 * Move the current player depending the vector given by argument.
	 * @param vector the vector to move.
	 */
	public void play(Vector vector) {
		Player p = getCurrentPlayer();
		if (vector == null) {
			log.debug("Vector is null");
			addFinishPlayer(p, false);
			if (updateIndexPlayerGame()) {
				computerPlay();
			}
			return ;
		}
		log.debug("Vector to play : {}", vector.toString());
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		p.incrementPlayingCounter();
		int x = getCoordinateLimit(p.getPosition().getX() + vector.getX());
		int y = getCoordinateLimit(p.getPosition().getY() - vector.getY());
		
		p.getPosition().setX(x);
		p.getPosition().setY(y);
		p.getVector().setX(vector.getX());
		p.getVector().setY(vector.getY());
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		computerPlay();
	}
	
	private int getCoordinateLimit(int coordinate) {
		if (coordinate < 0) {
			return 0;
		} else if (coordinate >= mapManager.getMapSize()) {
			return mapManager.getMapSize() - 1;
		}
		return coordinate;
	}
	
	/**
	 * Moves the last time the current player.
	 * @param vector the last vector to move.
	 */
	public void lastPlay(Vector vector) {
		log.debug("LastPlay {}", vector);
		Player p = getCurrentPlayer();
		p.incrementPlayingCounter();
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		Position endLinePosition = getIntersectionPosition(vector);
		log.debug("End position found : {}", endLinePosition);
		mapManager.getCase(endLinePosition.getY(), endLinePosition.getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		addFinishPlayer(p, true);
		if (!updateIndexPlayerGame()) {
			return ;
		}
		computerPlay();
	}
	
	/**
	 * Check the intersection between 2 segments to return a Position if it exists or null.
	 * The intersection is calculated by following these instructions :<br>
	 *  - k is the parameter of the intersection point from CD on AB, if k is between 0 and 1 the point in on CD<br>
	 *  - m is the parameter of the intersection point from AB on CD, if m is between 0 and 1 the point in on AB<br><br>
	 * I is the AB vector and J the CD vector. <br>
	 * P is the intersection Position like P=A+k*I or P=C+m*J <=> A + k*I = C + m*J <br>
	 * It could be decomposed in :<br>
	 *  - Ax + k*Ix = Cx + m*Jx <br>
	 *  - Ay + k*Iy = Cy + m*Jy <br><br>
	 * After resolving :
	 *  - m = -(-Ix*Ay+Ix*Cy+Iy*Ax-Iy*Cx)/(Ix*Jy-Iy*Jx)<br>
	 *  - k = -(Ax*Jy-Cx*Jy-Jx*Ay+Jx*Cy)/(Ix*Jy-Iy*Jx)<br><br>
	 *  If the denominator Ix*Jy-Iy*Jx = 0 the the both vectors are parallel else inject m or k in one
	 *  the equation to find intersection.<br><br>
	 *  <b>PS : </b> note that if m and k are higher than 1 or less than 0, the insection is on
	 *  the "droites" and not in the vector.
	 * @param vector the played vector, the next move.
	 * @return the intersection Position or null.
	 */
	private Position getIntersectionPosition(Vector v) {
		Position f1 = mapManager.getFinishingLinePositionList().get(0);
		Position f2 = mapManager.getFinishingLinePositionList().get(mapManager.getFinishingLinePositionList().size() - 1);
		Vector f1f2 = new Vector(f2.getX() - f1.getX(), f2.getY() - f1.getY());
		Vector vector = new Vector(v.getX(), - v.getY());
		log.trace("Position F1 {}, F2 {}, Vector f1f2 {}", new Object[]{f1,f2,f1f2});
		Player p = getCurrentPlayer();
		Position p1 = p.getPosition();
		double divider = vector.getX() * f1f2.getY() - vector.getY() * f1f2.getX();
		log.trace("Divider {}", divider);
		log.trace("Player's position : {}", p1);
		if (divider == 0) {
			return null;
		}
		double val =(-vector.getX() * p1.getY() + vector.getX() * f1.getY() + vector.getY() * p1.getX() - vector.getY() * f1.getX());
		log.trace("Dividende m : {}", val);
		double m = - val / divider;
		double val2 = (p1.getX() * f1f2.getY() - f1.getX() * f1f2.getY() - f1f2.getX() * p1.getY() + f1f2.getX() * f1.getY());
		log.trace("Dividende k : {}", val2);
		double k = - val2 / divider;
		log.trace("Coefficient of Vector played:{} ; end line Vector:{}", Double.toString(k), Double.toString(m));
		if (m >= 0 && m <= 1 && k >= 0 && k <= 1) {
			return new Position((int) (f1.getX() + m * f1f2.getX()), (int) (f1.getY() + m * f1f2.getY()));
		}
		return null;
	}
	
	private void addFinishPlayer(Player p, boolean isWinning) {
		int begin, end;
		log.trace("add player {} into finish list", p.toString());
		log.trace("{} is {}", p.toString(), isWinning ? "winning" : "losing");
		if (isWinning) {
			begin = 0;
			end = NUMBER_OF_PLAYER_MAX;
		} else {
			begin = NUMBER_OF_PLAYER_MAX - 1;
			end = 0;
		}
		log.trace("Initialization of loop from {} to {}", begin, end);
		for (int i = begin; (isWinning) ? i < end : i > end; i = (isWinning) ? i+1 : i-1) {
			if (finishPositionList.get(i) == null) {
				log.debug("the player {} finish at {} position", p.toString(), Integer.toString(i));
				finishPositionList.set(i, p);
				break;
			}
		}
	}
	
	/**
	 * Iterates all AI players and set new available position.
	 */
	public void computerPlay() {
		informationModel.addMessage(new InformationMessage(MessageType.STATS, "Round " + Integer.toString(getCurrentPlayer().getPlayingCounter() + 1)));
		while (getCurrentPlayer().getType() == PlayerType.COMPUTER && !finishPositionList.contains(getCurrentPlayer())) {
			Player p = getCurrentPlayer();
			log.debug("AI Player {} is under playing", p.toString());
			
			Vector vector = null;
			try {
				vector = computerLevel.getNextPlay(p, playerRoadPosition);
			} catch (FormulaMathException e) {
				e.printStackTrace();
			}
			if (vector == null) {
				p.incrementPlayingCounter();
				addFinishPlayer(p, false);
				if (!updateIndexPlayerGame()) {
					return;
				}
				continue ;
			}
			log.debug("Vector returned by AILevel: {}", vector.toString());
			log.trace("is last play? {}", computerLevel.isLastPlay());
			if (computerLevel.isLastPlay()) {
				lastPlay(vector);
				return;
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
							log.trace("computer is going through finish lane");
							lastPlay(new Vector(i, j));
							return;
						}
					}
				}
			}
			p.getPosition().setX(p.getPosition().getX() + vector.getX());
			p.getPosition().setY(p.getPosition().getY() - vector.getY());
			p.getVector().setX(vector.getX());
			p.getVector().setY(vector.getY());
			p.incrementPlayingCounter();
			mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
			if (!updateIndexPlayerGame()) {
				return;
			}
		}
		fireDisplayPlayerMovePossibilities();
	}
	
	private boolean updateIndexPlayerGame() {
		if (playerList.size() == getNumberOfFinishPlayer()) {
			log.debug("updateIndexPlayerGame : the players have finished");
			fireEndGameListener();
			return false;
		}
		do {
			indexPlayerGame++;
			indexPlayerGame = indexPlayerGame % playerList.size();
		} while(finishPositionList.contains(playerList.get(indexPlayerGame)));
		return true;
	}
	
	private int getNumberOfFinishPlayer() {
		int nb = 0;
		for (int i = 0; i < finishPositionList.size(); i++) {
			if (finishPositionList.get(i) != null) {
				log.trace("finishing list index= {} value = {}", Integer.toString(i), finishPositionList.get(i).toString());
				nb++;
			}
		}
		log.trace("Number of finishing player : {}", Integer.toString(nb));
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
					Position position = computerLevel.getStartingPosition();
					p.getPosition().setX(position.getX());
					p.getPosition().setY(position.getY());
					updateIndexPlayerGame();
					log.debug("computer start position : ({}, {})", p.getPosition().getX(), p.getPosition().getY());
					mapManager.getCase(position.getY(), position.getX()).setIdPlayer(p.getId());
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
		informationModel.pushMessage(new InformationMessage(MessageType.PLAYER, "Player " + player.getName() + " (" + player.getId() + ") must choose the start position"));
		for(UpdateCaseListener l : updateCaseListenerList) {
			l.displayPlayerStartingPossibilities(player, list, mapManager.getMapSize());
		}
	}
	
	private void fireDisplayPlayerFirstMovePossibilities(Player player) {
		informationModel.pushMessage(new InformationMessage(MessageType.PLAYER, "Player " + player.getName() + " (" + player.getId() + ") must choose the first move"));
		for(UpdateCaseListener l : updateCaseListenerList) {
			l.displayPlayerFirstMove(player, mapManager.getMapSize());
		}
	}
	
	private void fireDisplayPlayerMovePossibilities() {
		log.trace("Display moving possibilities for {}", getCurrentPlayer().toString());
		List<Vector> list = getVectorsPossibilities(getCurrentPlayer());
		if (list.isEmpty() && finishPositionList.contains(getCurrentPlayer())) {
			return;
		}
		informationModel.pushMessage(new InformationMessage(MessageType.PLAYER, "Player " + getCurrentPlayer().getName() + " (" + getCurrentPlayer().getId() + ") must choose the next move"));
		for(UpdateCaseListener l : updateCaseListenerList) {
			l.displayPlayerMovePossibilities(getCurrentPlayer(), list, mapManager.getMapSize());
		}
	}
	
	public void initFirstMove() {
		log.debug("initAIFirstMove");
		log.trace("currentPlayer : {}", getCurrentPlayer().toString());
		int index = getPlayerList().indexOf(getCurrentPlayer());
		informationModel.addMessage(new InformationMessage(MessageType.STATS, "Round " + Integer.toString(getCurrentPlayer().getPlayingCounter() + 1)));
		log.trace("current player index={}", index);
		List<Player> playerList = new ArrayList<Player>();
		log.trace("number of player : {}", getPlayerList().size());
		if (index < getPlayerList().size()) {
			playerList.addAll(getPlayerList().subList(index, getPlayerList().size()));
		}
		if (!playerList.isEmpty()) {
			Iterator<Player> it = playerList.iterator();
			while(it.hasNext()) {
				Player p = it.next();
				if (p.getType().equals(PlayerType.COMPUTER)) {
					log.debug("Computer first move");
					Vector vector = computerLevel.getFirstMove(p, playerRoadPosition);
					
					log.debug("Vector determined : {}", vector.toString());
					log.debug("AI initial position {}", p.getPosition().toString());
					mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
					p.getPosition().setX(p.getPosition().getX() + vector.getX());
					p.getPosition().setY(p.getPosition().getY() - vector.getY());
					p.getVector().setX(vector.getX());
					p.getVector().setY(vector.getY());
					p.incrementPlayingCounter();
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
		log.debug("Call computerPLayer(); before endding initFirstMove");
		computerPlay();
		log.debug("End of initFirstMove");
	}
	
	public void firstMove(Vector vector) {
		log.debug("First move {} for player {}", vector.toString(), getCurrentPlayer().toString());
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		getCurrentPlayer().getPosition().setX(getCurrentPlayer().getPosition().getX() + vector.getX());
		getCurrentPlayer().getPosition().setY(getCurrentPlayer().getPosition().getY() - vector.getY());
		getCurrentPlayer().getVector().setX(vector.getX());
		getCurrentPlayer().getVector().setY(vector.getY());
		getCurrentPlayer().incrementPlayingCounter();
		mapManager.getCase(getCurrentPlayer().getPosition().getY(), getCurrentPlayer().getPosition().getX()).setIdPlayer(getCurrentPlayer().getId());
		playerRoadPosition.put(getCurrentPlayer().getId(), 0);
		fireUpdateCaseListener(getCurrentPlayer());
		List<Player> playerList = getPlayerList().subList(getPlayerList().indexOf(getCurrentPlayer()), getPlayerList().size());
		updateIndexPlayerGame();
		if (getPlayerList().size() == 1 || playerList.size() == 1 || playerList.isEmpty()) {
			log.trace("Call computer play, player list is empty");
			computerPlay();
		} else {
			log.trace("Call init first move, player list is not empty, {} player(s) must choose the first move", playerList.size());
			initFirstMove();
		}
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

	/**
	 * Reinitializes all variable. It remove all data.
	 */
	public void fullReinitialization() {
		for (Player player : playerList) {
			player.fullReinitializationPlayer();
		}
		playerList.clear();
		finishPositionList.clear();
		playerRoadPosition.clear();
		indexPlayerGame = 0;
	}
	
	/**
	 * Reinitializes the player list to have the same players with new statistics.
	 */
	public void reinitialization() {
		for (Player player : playerList) {
			player.reinitializePlayer();
		}
		for (int i = 0; i < finishPositionList.size(); i++) {
			finishPositionList.set(i, null);
		}
		playerRoadPosition.clear();
		indexPlayerGame = 0;
		computerLevel.reinitIsLastPlay();
	}

	/**
	 * Set the Information Message model.
	 * @param informationModel the information model.
	 */
	public void setInformationMessageModel(InformationModel informationModel) {
		this.informationModel = informationModel;
	}

	/**
	 * Set an Artificial Intelligence (AIs are categorized in level).
	 * @param computerLevel the AI.
	 */
	public void setComputerLevel(AILevel computerLevel) {
		this.computerLevel = computerLevel;
	}
}
