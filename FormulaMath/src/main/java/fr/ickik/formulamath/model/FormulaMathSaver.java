package fr.ickik.formulamath.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import fr.ickik.formulamath.entity.Position;
import fr.ickik.formulamath.entity.RoadDirectionInformation;
import fr.ickik.formulamath.model.map.Field;
import fr.ickik.formulamath.model.map.MapManager;
import fr.ickik.formulamath.model.map.Orientation;

/**
 * This model saves and load map to permit the player to replay maps.
 * @author Ickik
 * @version 0.1.003, 31 July 2012
 * @since 0.2
 */
public final class FormulaMathSaver {

	private final Map<Integer, Field> valueFieldMap = new HashMap<Integer, Field>();
	private final Map<Field, Integer> fieldValueMap = new HashMap<Field, Integer>();
	private static final int BUFFER_SIZE = 4096;
	private static final FormulaMathSaver formulaMathSaver = new FormulaMathSaver();
	private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	private final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	
	private FormulaMathSaver() {
		for (Field f : Field.values()) {
			valueFieldMap.put(f.getValue(), f);
			fieldValueMap.put(f, f.getValue());
		}
	}
	
	/**
	 * Save the map contained in {@link MapManager} in the destinationFile parameter.
	 * @param manager the {@link MapManager} which contains the map characteristics.
	 * @param destinationFile the destination file.
	 * @return true if the map has been saved, false otherwise.
	 * @throws IOException this exception is thrown when an error occurs during saving.
	 */
	public boolean saveMap(MapManager manager, File destinationFile) throws IOException {
		StringBuilder str = new StringBuilder();
		str.append(getMapSizeString(manager.getMapSize()));
		str.append(getMapToStringBuilder(manager));
		str.append(getIdealWayToString(manager));
		FileOutputStream outputStream = new FileOutputStream(destinationFile);
		FileChannel channel = outputStream.getChannel();
		channel.write(encoder.encode(CharBuffer.wrap(str)));
		channel.close();
		outputStream.close();
		return true;
	}
	
	private StringBuilder getIdealWayToString(MapManager manager) {
		StringBuilder str = new StringBuilder();
		for (RoadDirectionInformation road : manager.getRoadDirectionInformationList()) {
			str.append(road.getOrientation().ordinal()).append(",");
			str.append(road.getBegin().getX()).append(",");
			str.append(road.getBegin().getY()).append(",");
			str.append(road.getEnd().getX()).append(",");
			str.append(road.getEnd().getY()).append(";");
			str.append("\n");
		}
		return str;
	}
	
	private StringBuilder getMapToStringBuilder(MapManager manager) {
		StringBuilder str = new StringBuilder();
		int size = manager.getMapSize();
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
		return str;
	}
	
	private String getMapSizeString(int size) {
		return Integer.toString(size) + "x" + Integer.toString(size) + "\n";
	}
	
	/**
	 * Load the file and constructs a {@link MapManager} with data.
	 * @param file the file to load.
	 * @return an instance of {@link MapManager}.
	 * @throws IOException is thrown if an error occurs the loading.
	 */
	public MapManager loadMap(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		FileChannel channel = inputStream.getChannel();
		ByteBuffer buffer =  ByteBuffer.allocate(BUFFER_SIZE);
		StringBuilder str = new StringBuilder();
		while (channel.read(buffer) > 0) {
			buffer.flip();
			str.append(decoder.decode(buffer));
			buffer.clear();
		}
		channel.close();
		inputStream.close();
		String[] array = str.toString().split("\n");
		int size = readSize(array[0]);
		MapManager manager = new MapManager();
		manager.init(size);
		int end = readMap(manager, array);
		end = readRoadIdealWay(manager, array, end);
		managerTreatment(manager);
		return manager;
	}
	
	private int readRoadIdealWay(MapManager manager, String[] array, int endIndex) {
		int size = array.length;
		int i;
		Pattern pattern = Pattern.compile("[0-3],[0-9]+,[0-9]+,[0-9]+,[0-9]+;");
		for (i = endIndex + 1; i < size && pattern.matcher(array[i]).matches(); i++) {
			String line = array[i];
			String[] values = line.split(",");
			int ordinal = Integer.parseInt(values[0]);
			Position begin = new Position(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
			Position end = new Position(Integer.parseInt(values[3]), Integer.parseInt(values[4].replace(";", "")));
			RoadDirectionInformation road = new RoadDirectionInformation(Orientation.values()[ordinal], begin, end);
			manager.getRoadDirectionInformationList().add(road);
		}
		return i;
	}
	
	private int readMap(MapManager manager, String[] array) {
		int size = manager.getMapSize();
		int i;
		for (i = 1; i < size; i++) {
			String line = array[i];
			for(int j = 0; j < size; j++) {
				Field field = valueFieldMap.get(Integer.parseInt(String.valueOf(line.charAt(j))));
				manager.getCase(i - 1, j).setField(field);
				if (field == Field.STARTING_LINE) {
					manager.getStartingPositionList().add(new Position(i - 1, j));
				} else if (field == Field.FINISHING_LINE) {
					manager.getFinishingLinePositionList().add(new Position(i - 1, j));
				}
			}
		}
		return i + 1;
	}
	
	private int readSize(String line) {
		return Integer.parseInt(line.split("x")[0]);
	}
	
	private void managerTreatment(MapManager manager) {
		Collections.sort(manager.getStartingPositionList(), getComparatorInstance());
		Collections.sort(manager.getFinishingLinePositionList(), getComparatorInstance());
		if (manager.getFinishingLinePositionList().size() > 2) {
			int len = manager.getFinishingLinePositionList().size() - 2;
			for (int i = 0; i < len; i++) {
				manager.getFinishingLinePositionList().remove(1);
			}
		}
	}
	
	private Comparator<Position> getComparatorInstance() {
		return new Comparator<Position>() {

			@Override
			public int compare(Position pos1, Position pos2) {
				if (pos1.getX() > pos2.getY()) {
					return 1;
				} else if (pos1.getX() < pos2.getY()) {
					return -1;
				}
				if (pos1.getY() > pos2.getY()) {
					return 1;
				} else if (pos1.getY() < pos2.getY()) {
					return -1;
				}
				return 0;
			}
		};
	}
	
	/**
	 * Return an unique instance of the class (singleton).
	 * @return an unique instance of the class.
	 */
	public static FormulaMathSaver getInstance() {
		return formulaMathSaver;
	}
}
