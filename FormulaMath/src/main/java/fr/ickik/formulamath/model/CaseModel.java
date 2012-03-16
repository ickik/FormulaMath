package fr.ickik.formulamath.model;

import fr.ickik.formulamath.Field;
import fr.ickik.formulamath.entity.Player;

/**
 * Model of the JCase component. It stores the type of the field and
 * the id of the player if one is this case.
 * @author Ickik.
 * @version 0.1.002, 3 dec 2011.
 */
public class CaseModel {

	private Field field = Field.GRASS;
	private int idPlayer = MapManager.EMPTY_PLAYER;

	/**
	 * Constructor of the model. It initializes the model with the field
	 * and the id of the empty player (0).
	 */
	public CaseModel() {}

	/**
	 * Set a new type of {@link Field} to the variable. A field
	 * is the type of the JCase. A case is a field with characteristics.
	 * @param field the new type of field.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	/**
	 * Return the field type of the model.
	 * @return the field type of the model.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Set the id of the {@link Player} who stay this case.
	 * @param idPlayer the id of the player.
	 */
	public void setIdPlayer(int idPlayer) {
		this.idPlayer = idPlayer;
	}

	/**
	 * Return the id of the {@link Player} who stay in this case
	 * or 0 when no player stays on this case.
	 * @return the id of the player.
	 */
	public int getIdPlayer() {
		return idPlayer;
	}

	/**
	 * Return true if a player is on this case or false otherwise.
	 * @return true if a player is on this case or false otherwise.
	 */
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
