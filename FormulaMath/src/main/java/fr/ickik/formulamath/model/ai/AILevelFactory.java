package fr.ickik.formulamath.model.ai;

import fr.ickik.formulamath.model.map.MapManager;

/**
 * 
 * @author Ickik
 * @version 0.1.000, 31 mai 2012
 * @since 0.3.1
 */
public class AILevelFactory {

	public static String[] LEVEL = new String[] {"Easy", "Medium", "Hard"};

	public static AILevel createLevelInstance(int level, MapManager mapManager) {
		switch (level) {
		case 0 :
			return new AIEasyLevel(mapManager);
		case 1:
			return new AIMediumLevel(mapManager);
		case 2:
			return new AIHighLevel(mapManager);
		default :
			return new AIMediumLevel(mapManager);
		}
	}
}
