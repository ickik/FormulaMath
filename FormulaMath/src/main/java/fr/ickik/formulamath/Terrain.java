package fr.ickik.formulamath;

import java.awt.Color;

public enum Terrain {

	ROUTE {
		@Override
		public Color getColor() {
			return Color.GRAY;
		}
	},

	HERBE {
		@Override
		public Color getColor() {
			return Color.GREEN;
		}
	},

	START_LINE {
		@Override
		public Color getColor() {
			return Color.YELLOW;
		}
	},

	END_LINE {
		@Override
		public Color getColor() {
			return Color.BLACK;
		}
	};

	public abstract Color getColor();
}
