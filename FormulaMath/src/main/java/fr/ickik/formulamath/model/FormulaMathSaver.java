package fr.ickik.formulamath.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;

/**
 * This model saves and load map to permit the player to replay maps.
 * @author Ickik
 * @version 0.1.000, 4 mai 2012
 * @since 0.2
 */
public final class FormulaMathSaver {

	private final Map<Integer, Field> valueFieldMap = new HashMap<Integer, Field>();
	private final Map<Field, Integer> fieldValueMap = new HashMap<Field, Integer>();
	private static final int BUFFER_SIZE = 4096;
	private static final FormulaMathSaver formulaMathSaver = new FormulaMathSaver();
	
	private FormulaMathSaver() {
		for (Field f : Field.values()) {
			valueFieldMap.put(f.getValue(), f);
			fieldValueMap.put(f, f.getValue());
		}
	}
	
	public boolean saveMap(MapManager manager, File destinationFile) throws IOException {
		int size = manager.getMapSize();
		StringBuilder str = new StringBuilder();
		str.append(size).append("x").append(size).append("\n");
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				CaseModel model = manager.getCase(i, j);
				if (model != null) {
					str.append(fieldValueMap.get(model.getField()));
				}
			}
			str.append("\n");
		}
		str.append("\n");
		ByteBuffer buffer = ByteBuffer.wrap(str.toString().getBytes());
		FileOutputStream outputStream = new FileOutputStream(destinationFile);
		FileChannel channel = outputStream.getChannel();
		channel.write(buffer);
		channel.close();
		outputStream.close();
		return true;
	}
	
	public MapManager loadMap(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		FileChannel channel = inputStream.getChannel();
		ByteBuffer buffer =  ByteBuffer.allocate(BUFFER_SIZE);
		StringBuilder str = new StringBuilder();
		while (channel.read(buffer) > 0) {
			buffer.flip();
			str.append(buffer.asCharBuffer());
			buffer.clear();
		}
		channel.close();
		inputStream.close();
		String[] array = str.toString().split("\n");
		int size = Integer.parseInt(array[0].split("x")[0]);
		MapManager manager = new MapManager();
		manager.init(size);
		for (int i = 1; i < size; i++) {
			String line = array[i];
			for(int j = 0; j < size; j++) {
				manager.getCase(i, j).setField(valueFieldMap.get(line.charAt(j)));
			}
		}
		return manager;
	}
	
	public static FormulaMathSaver getInstance() {
		return formulaMathSaver;
	}
}
