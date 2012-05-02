package fr.ickik.formulamath.model.ai;


import fr.ickik.formulamath.entity.Player;
import fr.ickik.formulamath.entity.Vector;

public interface AILevel {

	Vector getNextPlay(Player player);
}
