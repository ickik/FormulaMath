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

	public boolean setIdPlayer(int idPlayer) {
		this.idPlayer = idPlayer;
		return true;
	}

	public int getIdPlayer() {
		return idPlayer;
	}

	public boolean isOccuped() {
		if (idPlayer == 0) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		String str = new String();
		if (isOccuped()) {
			str = "Player ID : " + idPlayer + " - ";
		}
		return str.concat(getTerrain().toString());
	}
}
