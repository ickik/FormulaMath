package fr.ickik.formulamath;

/**
 * Model of the JCase component. It stores the type of the field and
 * the id of the player if one is this case.
 * @author Ickik.
 * @version 0.1.000, 3 dec 2011.
 */
public class CaseModel {

	private Field field;
	private int idPlayer;

	/**
	 * Constructor of the model. It initializes the model with the field
	 * and the id of the empty player (0).
	 * @param field the type of field of the model (this case).
	 */
	public CaseModel(Field field) {
		this.field = field;
		idPlayer = 0;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public boolean setIdPlayer(int idPlayer) {
		this.idPlayer = idPlayer;
		return true;
	}

	public int getIdPlayer() {
		return idPlayer;
	}

	public boolean isOccuped() {
		return idPlayer > 0;
	}
	
	@Override
	public String toString() {
		String str = new String();
		if (isOccuped()) {
			str = "Player ID : " + Integer.toString(idPlayer) + " - ";
		}
		return str.concat(getField().toString());
	}
}
