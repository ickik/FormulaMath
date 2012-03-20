package fr.ickik.formulamath.entity;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * Car represents the car picture which is associate to every player
 * through the id.
 * @author Ickik
 * @version 0.1.000, 16 mar 2012
 */
public class Car {
	private final int playerId;
	private final Image image;
	
	public Car(int playerId) {
		this.playerId = playerId;
		image = Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + ".jpg"));
	}

	/**
	 * Return the id of the player associates to this car.
	 * @return the id of the player.
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * The image of this car.
	 * @return the image of this car.
	 */
	public Image getImage() {
		return image;
	}
}
