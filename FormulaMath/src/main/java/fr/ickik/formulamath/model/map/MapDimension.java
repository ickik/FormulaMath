package fr.ickik.formulamath.model.map;

/**
 * Contains all dimension (number of cases) available for the map manager.
 * @author Ickik
 * @version 0.1.000, 3 mai 2012
 * @since 0.2
 */
public enum MapDimension {

	/**
	 * Small map (50 x 50)
	 */
	SMALL(50) {
		@Override
		public String toString() {
			return "Small (50 x 50)";
		}
	},

	/**
	 * Medium map (100 x 100)
	 */
	MEDIUM(100) {
		@Override
		public String toString() {
			return "Medium (100 x 100)";
		}
	},
	
	/**
	 * Big map (200 x 200)
	 */
	BIG(200) {
		@Override
		public String toString() {
			return "Big (200 x 200)";
		}
	};
	
	private final int value;
	
	private MapDimension(int value) {
		this.value = value;
	}
	
	/**
	 * Return the number of cases per side.
	 * @return the number of cases per side.
	 */
	public int getValue() {
		return value;
	}
}
