package fr.ickik.formulamath.model;

import java.awt.Color;

import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.view.JCase;

/**
 * Model of the {@link JCase} component. It stores the type of the field and
 * the id of the player if one is this case.
 * @author Ickik.
 * @version 0.1.007, 9 July 2012.
 */
public class CaseModel {

	private Field field;
	private int idPlayer = MapManager.EMPTY_PLAYER;
	private Color backgroundColor;
	private boolean paintBorder = true;
	private JCaseSide borderCaseSide;

	/**
	 * Constructor of the model. It initializes the model with the default
	 * field {@link Field#GRASS} and the id of the empty player (0).
	 * @param field the field displayed on the case.
	 */
	public CaseModel(Field field) {
		this.field = field;
	}

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
		return idPlayer != MapManager.EMPTY_PLAYER;
	}
	
	@Override
	public String toString() {
		String str = new String();
		if (isOccuped()) {
			str = "Player ID : " + Integer.toString(idPlayer) + " - ";
		}
		return str.concat(getField().toString());
	}

	/**
	 * Return the background Color for the component.
	 * @return the background color.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Set the background Color
	 * @param backgroundColor the new background Color.
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Return if the border of the component must be painted.
	 * @return true if the border must be painted.
	 */
	public boolean isPaintBorder() {
		return paintBorder;
	}

	/**
	 * Set if the border must be painted.
	 * @param paintBorder set true of the border should be painted or false otherwise.
	 */
	public void setPaintBorder(boolean paintBorder) {
		this.paintBorder = paintBorder;
	}

	/**
	 * 
	 * @return
	 */
	public JCaseSide getBorderCaseSide() {
		return borderCaseSide;
	}

	/**
	 * 
	 * @param borderCaseSide
	 */
	public void setBorderCaseSide(JCaseSide borderCaseSide) {
		this.borderCaseSide = borderCaseSide;
	}
}
