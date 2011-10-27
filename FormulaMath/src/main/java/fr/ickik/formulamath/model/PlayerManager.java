package fr.ickik.formulamath.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.FormulaMathException;
import fr.ickik.formulamath.Player;
import fr.ickik.formulamath.PlayerType;
import fr.ickik.formulamath.Position;
import fr.ickik.formulamath.StartFrame;
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
	private final MapManager mapManager;
	private boolean fireUpdateCaseListener;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();
	
	private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);

	public PlayerManager(int nbPlayer, MapManager mapManager) {
		this.mapManager = mapManager;
		playerList = new ArrayList<Player>(nbPlayer);
	}

	public void addPlayer(Player player) {
		playerList.add(player);
	}

	public Player getPlayer(int index) {
		return playerList.get(index);
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public List<Vector> getVectorsPossibilities(Player player) {
		log.debug("getVectorsPossibilities entering");
		List<Vector> list = new ArrayList<Vector>(5);
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving()) != null) {
			list.add(player.getVector());
		} else {
			list.add(null);
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() - 1) != null) {
			list.add(new Vector(player.getVector().getXMoving() - 1, player.getVector().getYMoving()));
		} else {
			list.add(null);
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving(), player.getPosition().getX() + player.getVector().getXMoving() + 1) != null) {
			list.add(new Vector(player.getVector().getXMoving() + 1, player.getVector().getYMoving()));
		} else {
			list.add(null);
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() + 1, player.getPosition().getX() + player.getVector().getXMoving()) != null) {
			list.add(new Vector(player.getVector().getXMoving(), player.getVector().getYMoving() - 1));
		} else {
			list.add(null);
		}
		if (mapManager.getCase(player.getPosition().getY() + player.getVector().getYMoving() - 1, player.getPosition().getX() + player.getVector().getXMoving()) != null) {
			list.add(new Vector(player.getVector().getXMoving(), player.getVector().getYMoving() + 1));
		} else {
			list.add(null);
		}
		log.debug("number of vectors : {}", list.size());
		log.debug("getVectorsPossibilities exiting");
		return list;
	}

	public boolean play(Vector vector) {
		Player p = playerList.get(indexPlayerGame);
		//Vector vector = getVectorsPossibilities(p).get(selection);
		if (mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()) == null
				|| mapManager.getCase(p.getPosition().getY() - vector.getYMoving(), p.getPosition().getX() + vector.getXMoving()) == null) {
			return false;
		}
		
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(MapManager.EMPTY_PLAYER);
		p.getPosition().setX(p.getPosition().getX() + vector.getXMoving());
		p.getPosition().setY(p.getPosition().getY() - vector.getYMoving());
		p.setXMoving(vector.getXMoving());
		p.setYMoving(vector.getYMoving());
		mapManager.getCase(p.getPosition().getY(), p.getPosition().getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(p);
		updateIndexPlayerGame();
		AIPlay();
		return true;
	}

	private void AIPlay() {
		while (playerList.get(indexPlayerGame).getType() == PlayerType.COMPUTER) {
			// jouer tout seul

			updateIndexPlayerGame();
		}
		play(playerList.get(indexPlayerGame));
	}
	
	private void updateIndexPlayerGame() {
		indexPlayerGame++;
		indexPlayerGame = indexPlayerGame % playerList.size();
	}
	
	private void play(Player human) {
		fireUpdatePossibilitiesListener(human);
	}

	public void initStartPosition() throws FormulaMathException {
		log.debug("initStartPosition entering");
		List<Position> list = mapManager.getStartPosition();
		log.debug("number of start position : {}", list.size());
		BitSet set = new BitSet(list.size());
		List<Player> playerList = getPlayerList();
		log.debug("" + playerList.size());
		for (Player p : playerList) {
			if (p.getType().equals(PlayerType.COMPUTER)) {
				int index = set.nextClearBit(0);
				p.getPosition().setX(list.get(index).getX());
				p.getPosition().setY(list.get(index).getY());
				set.set(index);
				mapManager.getCase(list.get(index).getY(), list.get(index).getX()).setIdPlayer(p.getId());
				fireUpdateCaseListener(list.get(index).getX(), list.get(index).getY(), p);
				log.debug("fire");
			} else {
				log.debug("set cardinality position : {}", set.cardinality());
				new StartFrame(this, list, set, p, mapManager);
				break;
			}
		}
		log.debug("initStartPosition exiting");
	}

	public void initStartPosition(Player player) throws FormulaMathException {
		log.debug("initStartPosition entering");
		List<Position> list = mapManager.getStartPosition();
		log.debug("number of start position : {}", list.size());
		BitSet set = new BitSet(list.size());
		List<Player> playerList = getPlayerList().subList(getPlayerList().indexOf(player) + 1, getPlayerList().size());
		if (!playerList.isEmpty()) {
			for (Player p : playerList) {
				log.debug("boucle for");
				if (p.getType().equals(PlayerType.COMPUTER)) {
					log.debug("computer");
					int index = set.nextClearBit(0);
					p.getPosition().setX(list.get(index).getX());
					p.getPosition().setY(list.get(index).getY());
					log.debug("computer start position : ({}, {})", p.getPosition().getX(), p.getPosition().getY());
					set.set(index);
					mapManager.getCase(list.get(index).getY(), list.get(index).getX()).setIdPlayer(p.getId());
					fireUpdateCaseListener(list.get(index).getX(), list.get(index).getY(), p);
					log.debug("fire");
				} else {
					log.debug("set cardinality position : {}", set.cardinality());
					new StartFrame(this, list, set, p, mapManager);
					break;
				}
			}
		} else {
			AIPlay();
		}
		log.debug("initStartPosition exiting");
	}
	
	/**
	 * Update the position of the player, depending the index of the available positions.
	 * @param p the player to update.
	 * @param index
	 * @throws FormulaMathException
	 */
	public void updatePlayer(Player p, int index) throws FormulaMathException {
		List<Position> list = mapManager.getStartPosition();
		p.getPosition().setX(list.get(index).getX());
		p.getPosition().setY(list.get(index).getY());
		mapManager.getCase(list.get(index).getY(), list.get(index).getX()).setIdPlayer(p.getId());
		fireUpdateCaseListener(list.get(index).getX(), list.get(index).getY(), p);
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

	protected void fireUpdateCaseListener(int x, int y, Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerCase(x, y, p);
		}
	}
	
	protected void fireUpdateCaseListener(Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerCase(p.getPosition().getX(), p.getPosition().getY(), p);
		}
	}
	
	protected void fireUpdatePossibilitiesListener(Player p) {
		for (UpdateCaseListener u : updateCaseListenerList) {
			u.updatePlayerPossibilities(p);
		}
	}

}
