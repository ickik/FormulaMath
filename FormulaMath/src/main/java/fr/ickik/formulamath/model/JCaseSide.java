package fr.ickik.formulamath.model;

/**
 * This enum has all positions in a JCase to draw at this place.
 * @author ickik
 * @version 0.1.000, 9 July 2012
 * @since 0.3.7
 */
public enum JCaseSide {

	/**
	 * Bottom side of the JCase.
	 */
	BOTTOM,
	
	/**
	 * The Left bottom corner but the direction is from NORTH to EAST (left) the angle is inside.
	 */
	BOTTOM_LEFT_CORNER_ACUTE,
	
	/**
	 * The Left bottom corner but the direction is from SOUTH to WEST (left) the angle is outside.
	 */
	BOTTOM_LEFT_CORNER_REFLEX,
	
	/**
	 * The Right bottom corner but the direction is from NORTH to WEST (right) the angle is inside.
	 */
	BOTTOM_RIGHT_CORNER_ACUTE,
	
	/**
	 * The Right bottom corner but the direction is from SOUTH to EAST (right) the angle is outside.
	 */
	BOTTOM_RIGHT_CORNER_REFLEX,
	
	/**
	 * Left side of the JCase.
	 */
	LEFT,
	
	/**
	 * Right side of the JCase.
	 */
	RIGHT,
	
	/**
	 * Top side of the JCase.
	 */
	TOP,
	
	/**
	 * The Top Left corner but the direction is from SOUTH to EAST (right) the angle is inside.
	 */
	TOP_LEFT_CORNER_ACUTE,
	
	/**
	 * The Top Left corner but the direction is from NORTH to WEST (right) the angle is outside.
	 */
	TOP_LEFT_CORNER_RELFEX,
	
	/**
	 * The Top Right corner but the direction is from SOUTH to WEST (left) the angle is inside.
	 */
	TOP_RIGHT_CORNER_ACUTE,
	
	/**
	 * The Top Right corner but the direction is from NORTH to EAST (left) the angle is outside.
	 */
	TOP_RIGHT_CORNER_REFLEX;
	
}
