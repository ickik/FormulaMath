package fr.ickik.formulamath.model;

import fr.ickik.formulamath.view.AbstractFormulaMathFrame;

/**
 * This enum lists all properties of the property file. It is used to store
 * player's data.
 * @author Ickik.
 * @version 0.1.009, 4th September 2012.
 */
public enum FormulaMathProperty {

//	/**
//	 * Define the color of the background of the 2 frames.
//	 */
//	BACKGROUND_COLOR {
//		@Override
//		public String toString() {
//			return "background.color";
//		}
//
//		@Override
//		public String getDefaultValue() {
//			return "-16777216";
//		}
//	
//	} ,
	
	/**
	 * The activation of the random sentence in the title of the frame.
	 */
	CHUCK_NORRIS_ACTIVATE {
		@Override
		public String toString() {
			return "chucknorris.activate";
		}

		@Override
		public String getDefaultValue() {
			return Boolean.FALSE.toString();
		}
	},
	
	/**
	 * The time between to change of sentence.
	 */
	CHUCK_NORRIS_TIME {
		@Override
		public String toString() {
			return "chucknorris.timer";
		}

		@Override
		public String getDefaultValue() {
			return "1";
		}
	},
	
	GRID_DISPLAYED {
		@Override
		public String toString() {
			return "grid.displayed";
		}

		@Override
		public String getDefaultValue() {
			return Boolean.TRUE.toString();
		}
	},
	
	/**
	 * Last update date.
	 */
	LAST_UPDATE {
		@Override
		public String toString() {
			return "last.update.date";
		}

		@Override
		public String getDefaultValue() {
			return "01/01/12";
		}
		
	},
	
	
	PLAYER1_NAME {
		@Override
		public String toString() {
			return "player1.name";
		}

		@Override
		public String getDefaultValue() {
			return "John";
		}
	},
	
	PLAYER2_NAME {
		@Override
		public String toString() {
			return "player2.name";
		}

		@Override
		public String getDefaultValue() {
			return "Dick";
		}
	},
	
	PLAYER3_NAME {
		@Override
		public String toString() {
			return "player3.name";
		}

		@Override
		public String getDefaultValue() {
			return "Malcolm";
		}
	},
	
	PLAYER4_NAME {
		@Override
		public String toString() {
			return "player4.name";
		}

		@Override
		public String getDefaultValue() {
			return "Todd";
		}
	},
	
	PLAYER1_TYPE {
		@Override
		public String toString() {
			return "player1.type.human";
		}

		@Override
		public String getDefaultValue() {
			return Boolean.TRUE.toString();
		}
	},
	
	PLAYER2_TYPE {
		@Override
		public String toString() {
			return "player2.type.human";
		}

		@Override
		public String getDefaultValue() {
			return Boolean.FALSE.toString();
		}
	},
	
	PLAYER3_TYPE {
		@Override
		public String toString() {
			return "player3.type.human";
		}

		@Override
		public String getDefaultValue() {
			return Boolean.FALSE.toString();
		}
	},
	
	PLAYER4_TYPE {
		@Override
		public String toString() {
			return "player4.type.human";
		}

		@Override
		public String getDefaultValue() {
			return Boolean.FALSE.toString();
		}
	},
	
	/**
	 * The theme of the UIManager. The theme available are depending on OS.
	 */
	THEME {
		@Override
		public String toString() {
			return "formulamath.theme";
		}
		
		@Override
		public String getDefaultValue() {
			return "javax.swing.plaf.metal.MetalLookAndFeel";
		}
	},
	
	/**
	 * Server address to update.
	 */
	UPDATE_SERVER {
		@Override
		public String toString() {
			return "update.server";
		}
		
		@Override
		public String getDefaultValue() {
			return "http://patrick.allgeyer.perso.sfr.fr";
		}
	},
	
	/**
	 * The version of the application.
	 */
	VERSION {
		@Override
		public String toString() {
			return "formulamath.version";
		}

		@Override
		public String getDefaultValue() {
			return AbstractFormulaMathFrame.VERSION;
		}
	};
	
	/**
	 * Return the property depending the value given in argument.
	 * @param value the FormulaMathProperty under string format.
	 * @return the FormulaMathProperty or null if it was not found.
	 */
	public static FormulaMathProperty getProperty(String value) {
		for (FormulaMathProperty p : FormulaMathProperty.values()) {
			if (p.toString().equals(value)) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Return the default value of the property.
	 * @return the default value of the property.
	 */
	public abstract String getDefaultValue();

}
