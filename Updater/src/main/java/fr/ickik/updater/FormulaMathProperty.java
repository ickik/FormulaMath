package fr.ickik.updater;

/**
 * This enum lists all properties of the property file. It is used to store
 * player's data.
 * @author Ickik.
 * @version 0.1.002, 26 mar. 2012.
 */
enum FormulaMathProperty {

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
			return "false";
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
	
	VERSION {

		@Override
		public String toString() {
			return "formulamath.version";
		}

		@Override
		public String getDefaultValue() {
			return "1.0.0";
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
