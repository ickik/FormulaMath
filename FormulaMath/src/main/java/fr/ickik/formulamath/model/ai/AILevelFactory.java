package fr.ickik.formulamath.model.ai;

import fr.ickik.formulamath.model.map.MapManager;

/**
 * Factory that creates a {@link AILevel} depending the level.
 * @author Ickik
 * @version 0.1.000, 31 mai 2012
 * @since 0.3.1
 */
public final class AILevelFactory {

	public static String[] LEVEL = new String[] {"Easy", "Medium", "Hard"};

	/**
	 * Creates an instance of {@link AILevel} depending the argument.
	 * If the argument is not relevant, a default {@link AILevel} should be created.
	 * @param level the level choose.
	 * @param mapManager the mapManager to have the way before defining a way.
	 * @return an instance of {@link AILevel}.
	 */
	public static AILevel createLevelInstance(int level, MapManager mapManager) {
		switch (level) {
		case 0 :
			return new AIEasyLevel(mapManager);
		case 1:
			return new AIMediumLevel(mapManager);
		case 2:
			return new AIHighLevel2(mapManager);
		default :
			return new AIMediumLevel(mapManager);
		}
	}
}
