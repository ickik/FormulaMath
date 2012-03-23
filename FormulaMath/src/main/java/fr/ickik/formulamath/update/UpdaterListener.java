package fr.ickik.formulamath.update;

public interface UpdaterListener {

	void updateValue(int value, String msg);

	void restart();
	
	void start();
}
