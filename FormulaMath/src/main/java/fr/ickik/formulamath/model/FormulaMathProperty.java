package fr.ickik.formulamath.model;

/**
 * This enum lists all properties of the property file. It is used to store
 * player's data.
 * @author Ickik.
 * @version 0.1.000, 13 dec. 2011.
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
	};
	
	/**
	 * Return the default value of the property.
	 * @return the default value of the property.
	 */
	public abstract String getDefaultValue();

}
