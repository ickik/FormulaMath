package fr.ickik.updater;

public interface UpdaterListener {

	void updateValue(int value, String msg);

	void restart();
	
	void start();
}
