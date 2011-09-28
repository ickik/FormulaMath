package fr.ickik.formulamath;

import java.awt.Color;

public class Player {

	private static int GENERAL_ID = 1;

	private final int id;
	private final Position position;
	private final Vector movingVector;
	private final String name;
	private final PlayerType type;
	private final Color[] colorList = new Color[] { Color.RED, Color.WHITE,
			Color.BLUE, Color.YELLOW };

	public Player(PlayerType type, String name) {
		id = GENERAL_ID++;
		this.type = type;
		this.name = name;
		position = new Position();
		movingVector = new Vector();
	}

	public int getId() {
		return id;
	}

	public Vector getVector() {
		return movingVector;
	}

	public Position getPosition() {
		return position;
	}

	public void setXMoving(int xMoving) {
		movingVector.setXMoving(xMoving);
	}

	public int getXMoving() {
		return movingVector.getXMoving();
	}

	public void setYMoving(int yMoving) {
		movingVector.setYMoving(yMoving);
	}

	public int getYMoving() {
		return movingVector.getYMoving();
	}

	public String getName() {
		return name;
	}

	public PlayerType getType() {
		return type;
	}

	/**
	 * Return the color of the player given in argument.
	 * 
	 * @param player
	 * @return
	 */
	public Color getPlayerColor() {
		return colorList[id - 1];
	}

}
