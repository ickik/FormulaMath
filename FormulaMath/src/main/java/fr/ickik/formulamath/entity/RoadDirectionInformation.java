package fr.ickik.formulamath.entity;

import fr.ickik.formulamath.model.map.Orientation;

/**
 * Object that stores information about the road. The combination of
 * many of these objects represents the modelisation of the road.
 * @author Ickik.
 * @version 0.1.001, 14 dec. 2011.
 */
public class RoadDirectionInformation {

	private final Position begin;
	private final Position end;
	private final Orientation orientation;
	
	/**
	 * Constructor of this object. It initializes all variable of
	 * this object.
	 * @param orientation the orientation of the road {@link Orientation}.
	 * @param begin the position of starting of this direction {@link Position}.
	 * @param end the position of the end of this direction {@link Position}.
	 */
	public RoadDirectionInformation(Orientation orientation, Position begin, Position end) {
		this.orientation = orientation;
		this.begin = begin;
		this.end = end;
	}

	/**
	 * Return the length between the start and the end positions.
	 * @return the length between the start and the end positions.
	 */
	public int getLength() {
		if (orientation == Orientation.SOUTH || orientation == Orientation.NORTH) {
			return Math.abs(begin.getY() - end.getY());
		}
		return Math.abs(begin.getX() - end.getX());
	}

	/**
	 * Return the orientation of the road.
	 * @return the orientation of the road.
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Return the start position of the road.
	 * @return the start position of the road.
	 */
	public Position getBegin() {
		return begin;
	}

	/**
	 * Return the end position of the road.
	 * @return the end position of the road.
	 */
	public Position getEnd() {
		return end;
	}
	
	@Override
	public String toString() {
		return orientation.toString() + " " + begin.toString() + " " + end.toString();
	}
	
	/**
	 * Return the distance from the position to the end.
	 * @param position the current position.
	 * @return the distance between position and the end.
	 */
	public int getLengthToEnd(Position position) {
		switch (orientation) {
		case NORTH:
		case SOUTH:
			return Math.abs(position.getY() - end.getY());
		case EAST:
		case WEST:
			return Math.abs(position.getX() - end.getX());
		}
		return 0;
	}
}
