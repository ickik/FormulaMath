package fr.ickik.formulamath.entity;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

/**
 * Car represents the car picture which is associate to every player
 * through the id.
 * @author Ickik
 * @version 0.1.001, 20 apr. 2012
 */
public class Car {
	private final int playerId;
	private final Image image;
//	private final HashMap<String, Image> orientationCarImageMap = new HashMap<String, Image>();
	
	public Car(int playerId) {
		this.playerId = playerId;
		AffineTransform transform = new AffineTransform();
		transform.rotate(2.0);
		
		image = Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + ".jpg"));
//		orientationCarImageMap.put(Orientation.NORTH.toString(), Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.NORTH + ".jpg")));
//		orientationCarImageMap.put(Orientation.SOUTH.toString(), Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.SOUTH + ".jpg")));
//		orientationCarImageMap.put(Orientation.EAST.toString(), Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.EAST + ".jpg")));
//		orientationCarImageMap.put(Orientation.WEST.toString(), Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.WEST + ".jpg")));
//		orientationCarImageMap.put(Orientation.NORTH + "_" + Orientation.EAST, Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.NORTH + "_" + Orientation.EAST + ".jpg")));
//		orientationCarImageMap.put(Orientation.NORTH + "_" + Orientation.WEST, Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.NORTH + "_" + Orientation.WEST + ".jpg")));
//		orientationCarImageMap.put(Orientation.SOUTH + "_" + Orientation.EAST, Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.SOUTH + "_" + Orientation.EAST + ".jpg")));
//		orientationCarImageMap.put(Orientation.SOUTH + "_" + Orientation.WEST, Toolkit.getDefaultToolkit().createImage(Car.class.getResource("car_" + Integer.toString(playerId) + "_" + Orientation.SOUTH + "_" + Orientation.WEST + ".jpg")));
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
	
//	public Image getImage(String orientation) {
//		return orientationCarImageMap.get(orientationCarImageMap);
//	}
}
