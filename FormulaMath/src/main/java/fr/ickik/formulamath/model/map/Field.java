package fr.ickik.formulamath.model.map;

import java.awt.Color;

/**
 * Type of field displayed in the map.
 * @author Ickik.
 * @version 0.1.002, 24 apr 2012.
 */
public enum Field {

	/**
	 * Road representation.
	 */
	ROAD(1) {
		@Override
		public Color getColor() {
			return Color.GRAY;
		}
	},

	/**
	 * Grass field.
	 */
	GRASS(0) {
		@Override
		public Color getColor() {
			return new Color(0, 200, 0);
//			return Color.GREEN;
		}
	},
	
	/**
	 * Sand field. Like bunker in golf.
	 */
	SAND(2) {
		@Override
		public Color getColor() {
			return new Color(255, 255, 100);
		}
	},

	/**
	 * Start lane representation.
	 */
	STARTING_LINE(5) {
		@Override
		public Color getColor() {
			return Color.YELLOW;
		}
	},

	/**
	 * Finishing lane type representation.
	 */
	FINISHING_LINE(9) {
		@Override
		public Color getColor() {
			return Color.BLACK;
		}
	};

	private final int value;
	
	private Field(int value) {
		this.value = value;
	}
	/**
	 * Return the default {@link Color} of the field.
	 * @return the default color of the field.
	 */
	public abstract Color getColor();
	
	/**
	 * Return the default value of the Field.
	 * @return the default value of the Field.
	 */
	public int getValue() {
		return value;
	}
}
