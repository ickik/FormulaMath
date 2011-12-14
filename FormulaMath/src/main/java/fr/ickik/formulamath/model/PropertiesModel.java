package fr.ickik.formulamath.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.JComponent;

import fr.ickik.formulamath.MainFrame;

public class PropertiesModel {
	private final String userPath;
	private final String directory = "." + MainFrame.NAME;
	private final String propertiesFile = "properties.properties";
	private final Properties properties;
	private final List<JComponent> frameList = new ArrayList<JComponent>();
	private static final PropertiesModel model = new PropertiesModel();
	
	private PropertiesModel() {
		userPath = System.getProperty("user.home");
		existingDirectory();
		this.properties = loadProperties();
	}
	
	public static PropertiesModel getInstance() {
		return model;
	}
	
	public void addComponent(JComponent frame) {
		frameList.add(frame);
	}
	
	public String getProperty(FormulaMathProperty property) {
		return properties.getProperty(property.toString());
	}
	
	public void putDefaultProperty(FormulaMathProperty property) {
		properties.put(property.toString(), property.getDefaultValue());
	}
	
	public void put(FormulaMathProperty property, String value) {
		properties.put(property.toString(), value);
	}
	
	public void save() throws IOException {
		File propertiesFile = new File(userPath + "/" + directory + "/" + this.propertiesFile);
		FileOutputStream out = new FileOutputStream(propertiesFile);
		try {
			properties.store(out, "Virtual ICTouch Computing file properties");
		} finally {
			out.close();
		}
	}
	
	private void existingDirectory() {
		File directoryFile = new File(userPath + "/" + directory);
		if (!directoryFile.exists()) {
			directoryFile.mkdir();
		}
	}
	
	private Properties loadProperties() {
		File propertiesFile = new File(userPath + "/" + directory + "/" + this.propertiesFile);
		return propertiesFileLoader(propertiesFile);
	}
	
	/**
	 * Load the property file given by argument and return a
	 * Properties Object.
	 * @param propertiesFile the property file.
	 * @return an instance of Properties.
	 * @throws ICTouchException if the file could not be read.
	 */
	private Properties propertiesFileLoader(File propertiesFile) {
		if (!propertiesFile.exists()) {
			try {
				return initDefaultProperties(propertiesFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return loadProperties(propertiesFile);
		}
		return null;
	}
	
	/**
	 * Define a default property file which all default properties.
	 * @param propertiesFile the property file to create and to
	 * initialize.
	 * @return an instance of Properties Object.
	 * @throws IOException if the save of the default properties in the
	 * file was not possible.
	 */
	private Properties initDefaultProperties(File propertiesFile) throws IOException {
		propertiesFile.createNewFile();
		Properties properties = new Properties();
		for (FormulaMathProperty p : FormulaMathProperty.values()) {
			properties.put(p.toString(), p.getDefaultValue());
		}
		FileOutputStream out = new FileOutputStream(propertiesFile);
		try {
			properties.store(out, "Virtual ICTouch Computing file properties");
		} finally {
			out.close();
		}
		return properties;
	}
	
	private Properties loadProperties(File propertiesFile) {
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream(propertiesFile);
			try {
				properties.load(in);
			} finally {
				in.close();
			}
		} catch (FileNotFoundException e) {
			//throw ICTouchException.createInstance(ICTouchExceptionID.PROPERTIES_FILE_NOT_EXISTS, e);
		} catch (IOException e) {
			//throw ICTouchException.createInstance(ICTouchExceptionID.PROPERTIES_FILE_LOADING_ERROR, e);
		}
		checkProperties(properties);
		return properties;
	}
	
	private void checkProperties(Properties properties) {
		List<String> propertiesList = new ArrayList<String>();
		for (FormulaMathProperty p : FormulaMathProperty.values()) {
			propertiesList.add(p.toString());
		}
		checkPropertiesKeys(properties, propertiesList);
		checkPropertiesValues(properties);
	}
	
	private void checkPropertiesKeys(Properties properties, List<String> propertiesList) {
		BitSet propertiesFound = new BitSet(propertiesList.size());
		List<String> failedPropertiesList = new ArrayList<String>();
		for (Object s : properties.keySet()) {
			if (!propertiesList.contains(s)) {
				failedPropertiesList.add((String) s);
			} else {
				propertiesFound.set(propertiesList.indexOf(s), true);
			}
		}
		for(String s : failedPropertiesList) {
			properties.remove(s);
		}
		int i = propertiesFound.nextClearBit(0);
		while (i < propertiesList.size()) {
			properties.put(FormulaMathProperty.values()[i].toString(), FormulaMathProperty.values()[i].getDefaultValue());
			i = propertiesFound.nextClearBit(i + 1);
		}
	}
	
	private void checkPropertiesValues(Properties properties) {
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String value = entry.getValue().toString();
			if (value.isEmpty()) {
				entry.setValue(FormulaMathProperty.valueOf(entry.getKey().toString()).getDefaultValue());
			}
		}
	}
}
