package fr.ickik.formulamath.entity;

/**
 * A vector is a movement with coordinates x and y.
 * x is the horizontal move and y the vertical move.
 * @author Ickik.
 * @version 0.1.001, 19 mar 2012.
 */
public class Vector {

	private int x;
	private int y;

	/**
	 * Default constructor which instantiates a new default vector.
	 * A default vector is a no moving vector with coordinates (0,0).
	 */
	public Vector() {
		this(0, 0);
	}

	/**
	 * Constructor to instantiate a vector with coordinates.
	 * @param x the horizontal coordinate.
	 * @param y the vertical coordinate.
	 */
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the horizontal move.
	 * @param x the horizontal move.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Return the horizontal coordinate.
	 * @return the horizontal coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set the vertical move.
	 * @param x the horizontal move.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Return the vertical coordinate.
	 * @return the vertical coordinate.
	 */
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "Vector moving x=" + Integer.toString(getX()) + ", y=" + Integer.toString(getY());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector) {
			Vector v = (Vector) obj;
			return x == v.getX() && y == v.getY();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return x ^ y * y;
	}
}
