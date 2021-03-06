package fr.ickik.formulamath.entity;

import java.util.Iterator;
import java.util.LinkedList;

import fr.ickik.formulamath.model.map.MapManager;

/**
 * The DetailledRoadDirectionList extends LinkedList to redefined the add method. This
 * redefinition is used to store the road constructed by the {@link MapManager}.
 * Every object are connected with coordinates one to the others. It should be
 * the best way of the road.
 * 
 * @author Ickik.
 * @version 0.1.001, 9 August 2012.
 * @since 0.3.9
 */
public class DetailledRoadDirectionList extends LinkedList<DetailledRoadDirectionInformation> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5090379331194590798L;

	@Override
	public boolean add(DetailledRoadDirectionInformation e) {
		if (e == null) {
			return false;
		}
		if (isEmpty()) {
			return super.add(e);
		}
		DetailledRoadDirectionInformation previous = peekLast();
		if (e.getInitialOrientation() == e.getOrientation() && previous.getInitialOrientation() == previous.getOrientation()) {
			previous.getEnd().setX(e.getEnd().getX());
			previous.getEnd().setY(e.getEnd().getY());
			return true;
		}
		e.getBegin().setX(previous.getEnd().getX());
		e.getBegin().setY(previous.getEnd().getY());
		return super.add(e);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		Iterator<DetailledRoadDirectionInformation> it = iterator();
		while (it.hasNext()) {
			str.append(it.next()).append("\n");
		}
		return str.toString();
	}
}