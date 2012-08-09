package fr.ickik.formulamath.entity;

import fr.ickik.formulamath.model.map.Orientation;

/**
 * Object that stores information about the road. The combination of
 * many of these objects represents the modelisation of the road.
 * @author Ickik.
 * @version 0.1.001, 9 August 2012.
 * @since 0.3.9
 */
public final class DetailledRoadDirectionInformation extends RoadDirectionInformation {

	private final Orientation initialOrientation;
	
	/**
	 * Constructor of this object. It initializes all variable of
	 * this object.
	 * @param orientation the orientation of the road {@link Orientation}.
	 * @param begin the position of starting of this direction {@link Position}.
	 * @param end the position of the end of this direction {@link Position}.
	 */
	public DetailledRoadDirectionInformation(Orientation initialOrientation, Orientation orientation, Position begin, Position end) {
		super(orientation, begin, end);
		this.initialOrientation = initialOrientation;
	}

	public Orientation getInitialOrientation() {
		return initialOrientation;
	}
	
	@Override
	public String toString() {
		return initialOrientation.toString() + "-" + super.getOrientation().toString() + " " + super.getBegin().toString() + " " + super.getEnd().toString();
	}
}
