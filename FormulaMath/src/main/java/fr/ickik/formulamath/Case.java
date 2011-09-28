package fr.ickik.formulamath;

public class Case {

	private Terrain terrain;
	private int idPlayer;

	public Case(Terrain terrain) {
		this.terrain = terrain;
		idPlayer = 0;
	}

	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}

	public Terrain getTerrain() {
		return terrain;
	}

	public void setIdPlayer(int idPlayer) throws FormulaMathException {
		if (terrain == Terrain.HERBE) {
			throw new FormulaMathException();
		}
		this.idPlayer = idPlayer;
	}

	public int getIdPlayer() {
		if (terrain == Terrain.HERBE) {
			return 0;
		}
		return idPlayer;
	}

	public boolean isOccuped() {
		if (idPlayer == 0) {
			return false;
		}
		return true;
	}
}
