package fr.ickik.formulamath.model.ai;

import java.awt.geom.Line2D;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.Vector;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.view.JCase;
import fr.ickik.formulamath.view.MainFrame;

/**
 * Abstract class which defines common methods for Artificial Intelligence Classes.
 * @author Ickik
 * @version 0.1.000, 27 August 2012
 * @since 0.3.9
 */
abstract class AbstractAILevel implements AILevel {

	private boolean isLastPlay = false;
	private final MapManager mapManager;
	private static final Logger log = LoggerFactory.getLogger(AbstractAILevel.class);
	
	public AbstractAILevel(MapManager mapManager) {
		this.mapManager = mapManager;
	}
	
	List<Vector> vectorListFiltered(Position position, List<Vector> list) {
		int marge = MainFrame.MAP_MARGIN / 2;
		int xTrayPanel = position.getX() + marge;
		int yTrayPanel = position.getY() + marge;
		log.trace("Player position :{}", position.toString());
		log.trace("Player position on map : ( {}, {} )", xTrayPanel, yTrayPanel);
		for (int i = 0; i < list.size();) {
			Vector v = list.get(i);
			if (v.getX() == 0 && v.getY() == 0) {
				list.remove(i);
				continue;
			}
			JCase c = mapManager.getCarteComponent().get(yTrayPanel).get(xTrayPanel);
			int y = getCoordinateLimit(yTrayPanel - v.getY(), mapManager.getMapSize());
			int x = getCoordinateLimit(xTrayPanel + v.getX(), mapManager.getMapSize());
			log.debug("solution : {}", list.get(i).toString());
			log.trace("Player final  position on map : ( {}, {} )", x, y);

			final JCase c2 = mapManager.getCarteComponent().get(y).get(x);
			Line2D line = new Line2D.Double(c.getX() + (c.getWidth() / 2), c.getY() + (c.getHeight() / 2), c2.getX() + (c.getWidth() / 2), c2.getY() + (c.getHeight() / 2));
			log.trace("Vector's line on map from ( {}, {} ) to ( {} , {} )", new Object[]{c.getX(), c.getY(), c2.getX(), c2.getY()});
			if (isGrassIntersection(line)) {
				log.trace("{} intersects grass", list.get(i).toString());
				list.remove(i);
			} else {
				i++;
			}
		}
		return list;
	}
	
	private boolean isGrassIntersection(Line2D shape) {
		log.debug("isGrassIntersection");
		return mapManager.checkIntersection(shape, Field.GRASS);
	}
	
	private int getCoordinateLimit(int coordinate, int mapSize) {
		if (coordinate < MainFrame.MAP_MARGIN / 2) {
			return MainFrame.MAP_MARGIN / 2;
		} else if (coordinate >= mapSize + (MainFrame.MAP_MARGIN / 2)) {
			return mapSize + (MainFrame.MAP_MARGIN / 2) - 1;
		}
		return coordinate;
	}

	public boolean isLastPlay() {
		return isLastPlay;
	}
	
	@Override
	public void reinitIsLastPlay() {
		this.isLastPlay = false;
	}
	
	@Override
	public Position getStartingPosition() {
		List<Position> list = mapManager.getStartingPositionList();
		if (mapManager.getRoadDirectionInformationList().size() == 1) {
			return list.remove(0);
		}
		int index = Math.round(list.size() / 2);
		return list.remove(index);
	}
}
