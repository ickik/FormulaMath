package fr.ickik.updater;

interface UpdaterListener {

	void updateValue(int value, String msg);

	void restart();
	
	void start();
}
