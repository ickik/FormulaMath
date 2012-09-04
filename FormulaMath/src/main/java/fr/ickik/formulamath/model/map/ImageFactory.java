package fr.ickik.formulamath.model.map;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ImageFactory {

	private static HashMap<Field, BufferedImage> imageMap = new HashMap<Field, BufferedImage>();
	
	static {
		//imageMap.put();
	}
	
	public static BufferedImage getImageInstance(Field field) {
		return imageMap.get(field);
	}
}
