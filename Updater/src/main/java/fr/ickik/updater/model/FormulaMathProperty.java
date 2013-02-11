package fr.ickik.updater.model;

/**
 * This enum lists all properties of the property file. It is used to store
 * player's data.
 * @author Ickik.
 * @version 0.1.003, 23 apr. 2012.
 */
enum FormulaMathProperty {
	
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
			return "0.2.1";
		}
	},
	
	UPDATER_VERSION {
		@Override
		public String toString() {
			return "updater.version";
		}

		@Override
		public String getDefaultValue() {
			return "1.0.1";
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
