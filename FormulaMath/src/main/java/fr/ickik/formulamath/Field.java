package fr.ickik.formulamath;

import java.awt.Color;

public enum Field {

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
	}, 
	
	NO_TERRAIN {

		@Override
		public Color getColor() {
			return Color.WHITE;
		}
		
	};

	public abstract Color getColor();
}