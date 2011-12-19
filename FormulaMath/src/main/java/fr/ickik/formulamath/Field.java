package fr.ickik.formulamath;

import java.awt.Color;

/**
 * Type of field displayed in the map.
 * @author Ickik.
 * @version 0.1.001, 1 oct 2011.
 */
public enum Field {

	ROAD {
		@Override
		public Color getColor() {
			return Color.GRAY;
		}
	},

	GRASS {
		@Override
		public Color getColor() {
			return Color.GREEN;
		}
	},

	STARTING_LINE {
		@Override
		public Color getColor() {
			return Color.YELLOW;
		}
	},

	FINISHING_LINE {
		@Override
		public Color getColor() {
			return Color.BLACK;
		}
	};

	/**
	 * Return the default {@link Color} of the field.
	 * @return the default color of the field.
	 */
	public abstract Color getColor();
}
