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

public class PlayerManager {

	private final List<Player> playerList;
	private int indexPlayerGame = 0;
	private final MapManager mapManager;
	private boolean fireUpdateCaseListener;
	private final List<UpdateCaseListener> updateCaseListenerList = new ArrayList<UpdateCaseListener>();

	private static final Logger log = LoggerFactory
			.getLogger(PlayerManager.class);

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
		list.add(player.getVector());
		list.add(new Vector(player.getVector().getXMoving() - 1, player
				.getVector().getYMoving()));
		list.add(new Vector(player.getVector().getXMoving() + 1, player
				.getVector().getYMoving()));
		list.add(new Vector(player.getVector().getXMoving(), player.getVector()
				.getYMoving() - 1));
		list.add(new Vector(player.getVector().getXMoving(), player.getVector()
				.getYMoving() + 1));
		log.debug("number of vectors : {}", list.size());
		log.debug("getVectorsPossibilities exiting");
		return list;
	}

	public void play(Vector vector) {
	}

	public void AIPlay() {
		while (playerList.get(indexPlayerGame).getType() == PlayerType.COMPUTER) {
			// jouer tout seul

			indexPlayerGame++;
			indexPlayerGame = indexPlayerGame % playerList.size();
		}
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
				mapManager.getCase(list.get(index).getY(),
						list.get(index).getX()).setIdPlayer(p.getId());
				fireUpdateCaseListener(list.get(index).getX(), list.get(index)
						.getY(), p);
				log.debug("fire");
			} else {
				log.debug("set cardinality position : {}", set.cardinality());
				new StartFrame(this, list, set, p, mapManager);
				break;
			}
		}
		log.debug("initStartPosition exiting");
	}

	public void updatePlayer(Player p, int index) throws FormulaMathException {
		List<Position> list = mapManager.getStartPosition();
		mapManager.getCase(list.get(index).getY(), list.get(index).getX())
				.setIdPlayer(p.getId());
		fireUpdateCaseListener(list.get(index).getX(), list.get(index).getY(),
				p);
	}

	public void initStartPosition(Player player) throws FormulaMathException {
		log.debug("initStartPosition entering");
		List<Position> list = mapManager.getStartPosition();
		log.debug("number of start position : {}", list.size());
		BitSet set = new BitSet(list.size());
		List<Player> playerList = getPlayerList().subList(
				getPlayerList().indexOf(player) + 1, getPlayerList().size());
		for (Player p : playerList) {
			log.debug("boucle for");
			if (p.getType().equals(PlayerType.COMPUTER)) {
				log.debug("computer");
				int index = set.nextClearBit(0);
				p.getPosition().setX(list.get(index).getX());
				p.getPosition().setY(list.get(index).getY());
				log.debug("computer start position : ({}, {})", p.getPosition()
						.getX(), p.getPosition().getY());
				set.set(index);
				mapManager.getCase(list.get(index).getY(),
						list.get(index).getX()).setIdPlayer(p.getId());
				fireUpdateCaseListener(list.get(index).getX(), list.get(index)
						.getY(), p);
				log.debug("fire");
			} else {
				log.debug("set cardinality position : {}", set.cardinality());
				new StartFrame(this, list, set, p, mapManager);
				break;
			}
		}
		log.debug("initStartPosition exiting");
	}

	public Color getColorById(int idPlayer) {
		return playerList.get(idPlayer - 1).getPlayerColor();
	}

	public void addUpdateCaseListener(UpdateCaseListener updateCaseListener)
			throws FormulaMathException {
		if (updateCaseListener == null) {
			throw new FormulaMathException();
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

}
